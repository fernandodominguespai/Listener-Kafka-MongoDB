package br.com.microservices.listenerMongoDB.orderservice.core.utils;

import br.com.microservices.listenerMongoDB.orderservice.core.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class JsonUtilTest{

    static final String JSON_OBJECT = """
        {
            "name": "test",
            "value": 123,
            "nested": { "active": true }
        }
        """;

    static final String JSON_ARRAY = "[{\"id\":1}, {\"id\":2}]";

    // Classe mock interna usada só para teste
    static class Dummy {
        public String name;
        public int value;
    }

    @Test
    void toJsonNode_shouldParseValidJson() {
        JsonNode node = JsonUtil.toJsonNode(JSON_OBJECT);
        assertThat(node.get("name").asText()).isEqualTo("test");
        assertThat(node.get("value").asInt()).isEqualTo(123);
        assertThat(node.get("nested").get("active").asBoolean()).isTrue();
    }

    @Test
    void toObject_shouldDeserializeToClass() {
        Dummy dummy = JsonUtil.toObject("{\"name\":\"abc\", \"value\":42}", Dummy.class);
        assertThat(dummy.name).isEqualTo("abc");
        assertThat(dummy.value).isEqualTo(42);
    }

    @Test
    void pretty_shouldFormatJson() {
        String pretty = JsonUtil.pretty("{\"x\":1,\"y\":true}");
        assertThat(pretty).contains("\n");
        assertThat(pretty).contains("x");
    }

    @Test
    void toJsonNode_shouldThrowForInvalidJson() {
        assertThatThrownBy(() -> JsonUtil.toJsonNode("not-json"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erro ao converter JSON");
    }

    @Test
    void toObject_shouldThrowForInvalidJson() {
        assertThatThrownBy(() -> JsonUtil.toObject("invalid", Dummy.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erro ao desserializar");
    }
}
