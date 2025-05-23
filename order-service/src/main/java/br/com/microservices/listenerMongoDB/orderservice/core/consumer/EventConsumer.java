package br.com.microservices.listenerMongoDB.orderservice.core.consumer;

import br.com.microservices.listenerMongoDB.orderservice.core.document.Event;
import br.com.microservices.listenerMongoDB.orderservice.core.document.MongoKey;
import br.com.microservices.listenerMongoDB.orderservice.core.document.Order;
import br.com.microservices.listenerMongoDB.orderservice.core.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
//@AllArgsConstructor
public class EventConsumer {

//    private final JsonUtil jsonUtil;

    @KafkaListener(topics = "mongo.ordersdb.orders", groupId = "order-group")
    public void listen(ConsumerRecord<String, String> record) {
        String key = record.key();
        String value = record.value();

        try {
            if (!StringUtils.hasText(value)) {
                log.warn("⚠️ Mensagem Kafka vazia. Ignorando...");
                return;
            }

            Event event = JsonUtil.toObject(value, Event.class);

            Order after = null;
            Order before = null;

            if (StringUtils.hasText(event.getAfter())) {
                after = JsonUtil.toObject(event.getAfter(), Order.class);
            }
            if (StringUtils.hasText(event.getBefore())) {
                before = JsonUtil.toObject(event.getBefore(), Order.class);
            }

            // Extrai o _id do Kafka KEY se before estiver null
            MongoKey mongoKey = JsonUtil.toObject(key, MongoKey.class);

            String operation = event.getOp();
            switch (operation) {
                case "c" -> handleCreate(after);
                case "u" -> handleUpdate(before, after, event.getUpdateDescription());
                case "d" -> {
                    if (event.getBefore() != null) {
                        handleDelete(before.getId());
                    } else {
                        String id = JsonUtil.toJsonNode(mongoKey.getId()).get("$oid").asText();
                        handleDelete(id);
                    }
                }
                default -> log.warn("⚠️ Operação desconhecida: {}", operation);
            }

        } catch (Exception e) {
            log.error("❌ Erro ao processar evento Kafka: {}", e.getMessage(), e);
        }
    }

    private void handleCreate(Order after) {
        log.info("🟢 Criado: {}", after);
    }

    private void handleUpdate(Order before, Order after, Event.UpdateDescription updateDescription) {
        log.info("🟡 Documento atualizado:");

        if (before != null) {
            log.info("Antes: {}", before);
        }
        if (after != null) {
            log.info("Depois: {}", after);
            if (after.getUpdatedAt() != null) {
                ZonedDateTime localTime = ZonedDateTime.ofInstant(after.getUpdatedAt(), ZoneId.of("America/Sao_Paulo"));
                log.info("⏰ Atualizado em horário local (BR): {}", localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        }

        if (updateDescription != null && StringUtils.hasText(updateDescription.getUpdatedFields())) {
            try {
                JsonNode updatedFields = JsonUtil.toJsonNode(updateDescription.getUpdatedFields());
                log.info("🔧 Campos atualizados:\n{}", updatedFields.toPrettyString());
            } catch (Exception e) {
                log.warn("⚠️ Falha ao ler updatedFields: {}", e.getMessage());
            }
        }
    }

    private void handleDelete(String id) {
        log.info("🔴 Documento deletado com _id: {}", id);
    }
}
