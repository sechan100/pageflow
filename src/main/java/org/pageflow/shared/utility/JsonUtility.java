package org.pageflow.shared.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @author : sechan
 */
public abstract class JsonUtility {
    private final static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());


    public static String toJson(Object object) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
