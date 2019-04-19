package io.battlesnake.starter.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class GameBoardUtilsTest {
    
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    @Mock int[][] boardMock;
    
    //@BeforeEach
    //public void setup(){
    //    MockitoAnnotations.initMocks(this);
    //}
    
    @Test
    void shouldReturnEmptyListWhenFoodIsNotInTheRequest() throws IOException {
        JsonNode request = JSON_MAPPER.readTree("{\"game\":{\"id\":\"4b6d94c6-ad93-491d-984b-0ef5f7cfc92d\"},\"turn\":9,\"board\":{\"height\":11,\"width\":11,\"food\":[],\"snakes\":[{\"id\":\"gs_4GFYtDPqHVj7SXdW948w4qXG\",\"name\":\"dmoneyUK / JDSnake\",\"health\":96,\"body\":[{\"x\":1,\"y\":10},{\"x\":1,\"y\":9},{\"x\":1,\"y\":8},{\"x\":1,\"y\":7}]}]},\"you\":{\"id\":\"gs_4GFYtDPqHVj7SXdW948w4qXG\",\"name\":\"dmoneyUK / JDSnake\",\"health\":96,\"body\":[{\"x\":1,\"y\":10},{\"x\":1,\"y\":9},{\"x\":1,\"y\":8},{\"x\":1,\"y\":7}]}}\n");
    
        List<int[]> actual = GameBoardUtils.markFood(boardMock, request);
        
        assertThat(actual).isEmpty();
    
    }
}