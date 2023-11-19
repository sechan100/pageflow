package org.pageflow.domain.book.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
abstract public class RearrangeRequest {

    private String type;
    private Long id;
    private String title;
    private Integer sortPriority;

}
