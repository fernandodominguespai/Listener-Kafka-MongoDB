package br.com.microservices.listenerMongoDB.orderservice.core.consumer;

import br.com.microservices.listenerMongoDB.orderservice.core.consumer.EventConsumer;
import br.com.microservices.listenerMongoDB.orderservice.core.utils.JsonUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

public class EventConsumerTest {

    private final EventConsumer consumer = new EventConsumer();

    @Test
    void shouldProcessValidUpdateEventWithoutError() {
        String key = "{\"id\":\"{\\\"$oid\\\": \\\"68122a1c92da88b53346576c\\\"}\"}";

        String value = """
            {
              "before": null,
              "after": "{\\"_id\\": {\\"$oid\\": \\"68122a1c92da88b53346576c\\"},\\"id\\": \\"64429e987a8b646915b3735f\\",\\"products\\": [{\\"product\\": {\\"code\\": \\"COMIC_BOOKS\\",\\"unitValue\\": 15.5},\\"quantity\\": 3},{\\"product\\": {\\"code\\": \\"BOOKS\\",\\"unitValue\\": 9.9},\\"quantity\\": 1}],\\"totalItems\\": 0,\\"totalAmount\\": 25.4,\\"createdAt\\": \\"2025-04-29T14:32:56.335943085\\",\\"updatedAt\\": {\\"$date\\": 1746035995921}}",
              "updateDescription": {
                "removedFields": null,
                "updatedFields": "{\\"totalItems\\": 0, \\"updatedAt\\": {\\"$date\\": \\"2025-04-30T17:33:30.61Z\\"}}",
                "truncatedArrays": null
              },
              "source": {
                "version": "2.7.0.Final",
                "connector": "mongodb",
                "name": "mongo",
                "ts_ms": 1746035995000,
                "snapshot": "false",
                "db": "ordersdb",
                "collection": "orders",
                "ord": 1
              },
              "op": "u",
              "ts_ms": 1746035995923,
              "transaction": null
            }
            """;

        var record = new ConsumerRecord<>("mongo.ordersdb.orders", 0, 0L, key, value);

        assertThatCode(() -> consumer.listen(record))
                .doesNotThrowAnyException();
    }
}
