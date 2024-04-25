package org.pageflow.boundedcontext.email;


import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class EmailRequest {
    
    private String from;
    private String fromName;
    private String to;
    private String subject;
    private String template;
    private Map<String, Object> models = new HashMap<>();
    
    public void addModel(String key, Object value) {
        models.put(key, value);
    }

}