package org.pageflow.domain.book.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RearrangeRequest {

    private String type;
    private Long id;
    private Integer sortPriority;

}
