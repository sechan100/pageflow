package org.pageflow.infra.email;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    
    private String from;
    private String to;
    private String subject;
    private String template;
    private Map<String, Object> models;

}