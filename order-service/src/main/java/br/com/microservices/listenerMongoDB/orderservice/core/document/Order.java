package br.com.microservices.listenerMongoDB.orderservice.core.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class Order {

    @JsonProperty("_id")
    private OidWrapper mongoId;

    private String id;
    private List<OrderProducts> products;
    private int totalItems;
    private double totalAmount;

    @JsonDeserialize(using = MongoDateDeserializer.class)
    private Instant createdAt;

    @JsonDeserialize(using = MongoDateDeserializer.class)
    private Instant updatedAt;

}

