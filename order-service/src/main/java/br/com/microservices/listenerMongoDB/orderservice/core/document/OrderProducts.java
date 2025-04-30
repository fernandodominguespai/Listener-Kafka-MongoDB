package br.com.microservices.listenerMongoDB.orderservice.core.document;

import lombok.Data;

@Data
public class OrderProducts {
    private Product product;
    private int quantity;
}