package br.com.microservices.listenerMongoDB.orderservice.core.document;

import lombok.Data;

@Data
public class Product {
    private String code;
    private double unitValue;
}