package br.com.microservices.listenerMongoDB.orderservice.core.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OidWrapper {

    @JsonProperty("$oid")
    private String oid;
}
