package br.com.microservices.listenerMongoDB.orderservice.core.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MongoKey {
    private String id;
}
