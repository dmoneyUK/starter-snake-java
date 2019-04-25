package io.battlesnake.starter.handler;

import com.fasterxml.jackson.databind.JsonNode;
import io.battlesnake.starter.utils.JsonFixtures;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ShouldAvoidSelfCollisionAndMoveDown extends SnakeAppHandlerTest{
    
    @Test
    void shouldAvoidSelfCollisionAndMoveDown() throws IOException {
        String reqJson = JsonFixtures.read("fixtures/shouldAvoidSelfCollision.json");
        
        JsonNode request = JSON_MAPPER.readTree(reqJson);
        
        Map<String, String> response = testObj.move(request);
        assertThat(response.get("move")).isEqualTo("down");
    }
    
    @Test
    void shouldAvoidSelfCollision2AndMoveRight() throws IOException {
        String reqJson = JsonFixtures.read("fixtures/shouldAvoidSelfCollision2.json");
        
        JsonNode request = JSON_MAPPER.readTree(reqJson);
        
        Map<String, String> response = testObj.move(request);
        assertThat(response.get("move")).isEqualTo("right");
    }
}
