package io.battlesnake.starter.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.battlesnake.starter.utils.JsonFixtures;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

class shouldGoNearestFood {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private SnakeHandler testObj = new SnakeHandler();
    
    @Test
    void shouldMoveUp() throws Exception {
        String reqJson = JsonFixtures.read("fixtures/shouldGoNearestFood.json");
        
        JsonNode request = JSON_MAPPER.readTree(reqJson);
        
        Map<String, String> response = testObj.move(request);
        assertThat(response.get("move")).isEqualTo("down");
    }
}
