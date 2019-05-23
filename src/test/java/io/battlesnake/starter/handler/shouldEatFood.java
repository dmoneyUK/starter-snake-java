package io.battlesnake.starter.handler;

import com.fasterxml.jackson.databind.JsonNode;
import io.battlesnake.starter.utils.JsonFixtures;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

class shouldEatFood extends SnakeAppHandlerTest {
    
    @Test
    void shouldEatFood() throws Exception {
        String reqJson = JsonFixtures.read("fixtures/shouldEatFood.json");
        
        JsonNode request = JSON_MAPPER.readTree(reqJson);
        
        Map<String, String> response = testObj.move(request);
        assertThat(response.get("move")).isNotEqualTo("left");
    }
 
}
