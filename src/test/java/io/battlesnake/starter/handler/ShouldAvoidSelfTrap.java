package io.battlesnake.starter.handler;

import com.fasterxml.jackson.databind.JsonNode;
import io.battlesnake.starter.utils.JsonFixtures;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ShouldAvoidSelfTrap extends SnakeAppHandlerTest {
    
    @Test
    void shouldAvoidSelfTrap() throws Exception {
        String reqJson = JsonFixtures.read("fixtures/shouldAvoidSelfTrap.json");

        JsonNode request = JSON_MAPPER.readTree(reqJson);

        Map<String, String> response = testObj.move(request);
        assertThat(response.get("move")).isEqualTo("down");
    }
    
    @Test
    void shouldAvoidSelfTrap2() throws Exception {
        String reqJson = JsonFixtures.read("fixtures/shouldAvoidSelfTrap2.json");
        
        JsonNode request = JSON_MAPPER.readTree(reqJson);
        
        Map<String, String> response = testObj.move(request);
        assertThat(response.get("move")).isEqualTo("right");
    }
    
    @Test
    void shouldAvoidSelfTrap3() throws Exception {
        String reqJson = JsonFixtures.read("fixtures/shouldAvoidSelfTrap3.json");
        
        JsonNode request = JSON_MAPPER.readTree(reqJson);
        
        Map<String, String> response = testObj.move(request);
        assertThat(response.get("move")).isEqualTo("up");
    }
    
    @Test
    void shouldAvoidSelfTrap4() throws Exception {
        String reqJson = JsonFixtures.read("fixtures/shouldAvoidSelfTrap4.json");
        
        JsonNode request = JSON_MAPPER.readTree(reqJson);
        
        Map<String, String> response = testObj.move(request);
        assertThat(response.get("move")).isEqualTo("up");
    }
    
}
