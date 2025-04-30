package br.com.microservices.listenerMongoDB.orderservice.core.document;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.Instant;

public class MongoDateDeserializer extends JsonDeserializer<Instant> {

    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // Caso 1: Objeto com campo $date
        if (node.isObject() && node.has("$date")) {
            JsonNode dateNode = node.get("$date");

            if (dateNode.isNumber()) {
                return Instant.ofEpochMilli(dateNode.asLong());
            } else if (dateNode.isTextual()) {
                return Instant.parse(truncateNanos(dateNode.asText()));
            } else {
                throw new IOException("Formato de $date inválido: " + dateNode.toString());
            }
        }

        // Caso 2: String ISO direta
        if (node.isTextual()) {
            return Instant.parse(truncateNanos(node.asText()));
        }

        throw new IOException("Formato de data não suportado: " + node.toString());
    }

    private String truncateNanos(String iso) {
        int dotIndex = iso.indexOf('.');
        if (dotIndex == -1) {
            return iso.endsWith("Z") ? iso : iso + "Z";
        }

        int end = iso.indexOf('Z', dotIndex);
        if (end == -1) end = iso.length();

        String prefix = iso.substring(0, dotIndex + 1);
        String nanos = iso.substring(dotIndex + 1, end);

        // Trunca para no máximo 6 dígitos
        String truncated = nanos.length() > 6 ? nanos.substring(0, 6) : nanos;

        // Garante que termina com Z
        return prefix + truncated + (iso.endsWith("Z") ? "" : "Z");
    }

}