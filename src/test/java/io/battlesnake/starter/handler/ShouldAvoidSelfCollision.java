package io.battlesnake.starter.handler;

import com.fasterxml.jackson.databind.JsonNode;
import io.battlesnake.starter.utils.JsonFixtures;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ShouldAvoidSelfCollision extends SnakeAppHandlerTest{
    
    @Test
    void shouldAvoidSelfCollisionAndMoveDown() throws Exception {
        String reqJson = JsonFixtures.read("fixtures/shouldAvoidSelfCollision.json");
        
        JsonNode request = JSON_MAPPER.readTree(reqJson);
        
        Map<String, String> response = testObj.move(request);
        assertThat(response.get("move")).isEqualTo("down");
    }
    
    @Test
    void shouldAvoidSelfCollision2AndMoveRight() throws Exception {
        String reqJson = JsonFixtures.read("fixtures/shouldAvoidSelfCollision2.json");
        
        JsonNode request = JSON_MAPPER.readTree(reqJson);
        
        Map<String, String> response = testObj.move(request);
        assertThat(response.get("move")).isEqualTo("right");
    }
    
    
    //@Test
    //void shouldAvoidSelfCollision3AndMoveDown() throws IOException {
    //    String reqJson = JsonFixtures.read("fixtures/shouldAvoidSelfCollision3.json");
    //
    //    JsonNode request = JSON_MAPPER.readTree(reqJson);
    //
    //    Map<String, String> response = testObj.move(request);
    //    assertThat(response.get("move")).isEqualTo("down");
    //}
    
    @Test
    void shouldAvoidSelfCollision4AndMoveRight() throws Exception {
        String reqJson = JsonFixtures.read("fixtures/shouldAvoidSelfCollision4.json");
        
        JsonNode request = JSON_MAPPER.readTree(reqJson);
        
        Map<String, String> response = testObj.move(request);
        assertThat(response.get("move")).isEqualTo("right");
    }
    
    @Test
    void shouldAvoidSelfCollision5AndMoveDown() throws Exception {
        String reqJson = JsonFixtures.read("fixtures/shouldAvoidSelfCollision5.json");

        JsonNode request = JSON_MAPPER.readTree(reqJson);

        Map<String, String> response = testObj.move(request);
        assertThat(response.get("move")).isNotEqualTo("right");
    }
    
    @Test
    void shouldAvoidSelfCollision6AndMoveRight() throws Exception {
        String reqJson = JsonFixtures.read("fixtures/shouldAvoidSelfCollision6.json");
        
        JsonNode request = JSON_MAPPER.readTree(reqJson);
        
        Map<String, String> response = testObj.move(request);
        assertThat(response.get("move")).isEqualTo("right");
    }

    @Test
    void shouldAvoidSelfCollision7() throws Exception {
        String reqJson = JsonFixtures.read("fixtures/shouldAvoidSelfCollision7.json");

        JsonNode request = JSON_MAPPER.readTree(reqJson);

        Map<String, String> response = testObj.move(request);
        assertThat(response.get("move")).isEqualTo("right");
    }
    
    @Test
    void shouldAvoidSelfCollision8() throws Exception {
        String reqJson = JsonFixtures.read("fixtures/shouldAvoidSelfCollision8.json");
        
        JsonNode request = JSON_MAPPER.readTree(reqJson);
        
        Map<String, String> response = testObj.move(request);
        assertThat(response.get("move")).isNotEqualTo("left");
    }
    
    @Test
    void shouldAvoidSelfCollision9() throws Exception {
        String reqJson = JsonFixtures.read("fixtures/shouldAvoidSelfCollision9.json");
        
        JsonNode request = JSON_MAPPER.readTree(reqJson);
        
        Map<String, String> response = testObj.move(request);
        assertThat(response.get("move")).isNotEqualTo("left");
    }
    
    @Test
    void shouldAvoidSelfCollision10() throws Exception {
        String reqJson = JsonFixtures.read("fixtures/test.json");
        
        JsonNode request = JSON_MAPPER.readTree(reqJson);
        
        Map<String, String> response = testObj.move(request);
        assertThat(response.get("move")).isNotEqualTo("right");
    }
}
