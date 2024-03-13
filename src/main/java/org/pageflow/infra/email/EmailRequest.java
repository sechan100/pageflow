package org.pageflow.infra.email;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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