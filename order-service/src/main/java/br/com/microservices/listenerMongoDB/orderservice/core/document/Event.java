package br.com.microservices.listenerMongoDB.orderservice.core.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

    private String before; // será tratado como JSON string
    private String after;

    @JsonProperty("updateDescription")
    private UpdateDescription updateDescription;

    private Source source;
    private String op;
    private Long ts_ms;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UpdateDescription {
        @JsonProperty("updatedFields")
        private String updatedFields;
        @JsonProperty("removedFields")
        private String removedFields;
        @JsonProperty("truncatedArrays")
        private String truncatedArrays;
    }
}