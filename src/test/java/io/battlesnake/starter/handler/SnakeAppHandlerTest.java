package io.battlesnake.starter.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.battlesnake.starter.utils.JsonFixtures;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

class SnakeAppHandlerTest {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private SnakeHandler testObj = new SnakeHandler();
    
    @Test
    void shouldMoveUp() throws IOException {
        String reqJson = " {\"game\":{\"id\":\"4daf0360-abc2-4074-9a32-e43c26f91bf1\"},\"turn\":0,\"board\":{\"height\":11,\"width\":11,\"food\":[{\"x\":2,\"y\":8},{\"x\":2,\"y\":10},{\"x\":4,\"y\":5},{\"x\":8,\"y\":8},{\"x\":2,\"y\":1}],\"snakes\":[{\"id\":\"gs_xtk944dkw8hkfMG63gCMvrC9\",\"name\":\"Crypter03 / The Ultimate Snake\",\"health\":100,\"body\":[{\"x\":1,\"y\":1},{\"x\":1,\"y\":1},{\"x\":1,\"y\":1}]},{\"id\":\"gs_TD6VJBqmb9S4pTvBb4Vw4CbB\",\"name\":\"jnorth / help!\",\"health\":100,\"body\":[{\"x\":9,\"y\":9},{\"x\":9,\"y\":9},{\"x\":9,\"y\":9}]},{\"id\":\"gs_rkftvY77MQjKWHMgxdCq9ck4\",\"name\":\"raspygold / Snaketopus\",\"health\":100,\"body\":[{\"x\":1,\"y\":9},{\"x\":1,\"y\":9},{\"x\":1,\"y\":9}]},{\"id\":\"gs_qfYjjHVhtkfgj3cXFb9Prq9Y\",\"name\":\"bgdwyer / Irwin\",\"health\":100,\"body\":[{\"x\":9,\"y\":1},{\"x\":9,\"y\":1},{\"x\":9,\"y\":1}]},{\"id\":\"gs_BrGkmhXB48YpTYTF7MDDMKDQ\",\"name\":\"dmoneyUK / JDSnake\",\"health\":100,\"body\":[{\"x\":5,\"y\":1},{\"x\":5,\"y\":1},{\"x\":5,\"y\":1}]}]},\"you\":{\"id\":\"gs_BrGkmhXB48YpTYTF7MDDMKDQ\",\"name\":\"dmoneyUK / JDSnake\",\"health\":100,\"body\":[{\"x\":5,\"y\":1},{\"x\":5,\"y\":1},{\"x\":5,\"y\":1}]}}";
        
        JsonNode request = JSON_MAPPER.readTree(reqJson);
        
        Map<String, String> response = testObj.move(request);
        assertThat(response.get("move")).isEqualTo("left");
    }
    
    @Test
    void shouldAvoidHeadToHeadAndMoveDown() throws IOException {
        String reqJson = JsonFixtures.read("fixtures/shouldAvoidHeadToHead.json");
        
        JsonNode request = JSON_MAPPER.readTree(reqJson);
        
        Map<String, String> response = testObj.move(request);
        assertThat(response.get("move")).isEqualTo("down");
    }
    
    @Test
    void shouldAvoidWallCollisionAndMoveDown() throws IOException {
        String reqJson = JsonFixtures.read("fixtures/shouldAvoidWallCollison.json");
        
        JsonNode request = JSON_MAPPER.readTree(reqJson);
        
        Map<String, String> response = testObj.move(request);
        assertThat(response.get("move")).isEqualTo("down");
    }
    
    @Test
    void shouldAvoidSelfCollisionAndMoveDown() throws IOException {
        String reqJson = JsonFixtures.read("fixtures/shouldAvoidSelfCollision.json");
        
        JsonNode request = JSON_MAPPER.readTree(reqJson);
        
        Map<String, String> response = testObj.move(request);
        assertThat(response.get("move")).isEqualTo("down");
    }
    
}
