package br.com.microservices.listenerMongoDB.orderservice.core.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T toObject(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao desserializar JSON para " + clazz.getSimpleName(), e);
        }
    }

    public static JsonNode toJsonNode(String json) {
        try {
            return mapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter JSON em JsonNode", e);
        }
    }

    public static String pretty(String json) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(toJsonNode(json));
        } catch (Exception e) {
            return json; // retorna bruto em caso de erro
        }
    }
}