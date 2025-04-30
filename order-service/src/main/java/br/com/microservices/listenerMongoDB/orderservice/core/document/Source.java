package br.com.microservices.listenerMongoDB.orderservice.core.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Source {
    private String version;
    private String connector;
    private String name;
    private String db;
    private String collection;
}