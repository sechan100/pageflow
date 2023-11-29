package org.pageflow.domain.book.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
abstract public class RearrangeRequest {

    private String type;
    
    @NotEmpty
    private Long id;
    
    private String title;
    
    private Integer sortPriority;

    public RearrangeRequest(String type) {
        this.type = type;
    }
}