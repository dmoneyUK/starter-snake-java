package io.battlesnake.starter.utils;

import spark.resource.ClassPathResource;
import spark.utils.IOUtils;

import java.io.IOException;

public class JsonFixtures {
    
    public static String read(String filename) throws IOException {
        
        ClassPathResource dsl = new ClassPathResource(filename);
        
        String text = IOUtils.toString(dsl.getInputStream());
        
        return text;
    }
}
