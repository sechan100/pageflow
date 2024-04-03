package org.pageflow.shared.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @author : sechan
 */
public abstract class JsonUtil {
    private final static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());


    public static void toJson(Object object) {
        try {
            System.out.println(
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
