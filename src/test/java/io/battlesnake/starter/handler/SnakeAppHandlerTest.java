package io.battlesnake.starter.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.battlesnake.starter.utils.JsonFixtures;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

class SnakeAppHandlerTest {
    protected static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    protected SnakeHandler testObj = new SnakeHandler();
    
}
