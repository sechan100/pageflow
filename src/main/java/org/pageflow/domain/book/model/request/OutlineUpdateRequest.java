package org.pageflow.domain.book.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.pageflow.domain.book.model.outline.ChapterSummary;

import java.util.List;

/**
 * Outline 재정렬에 필요한 데이터만을 가지고있는 dto
 * @author : sechan
 */
@Data
public class OutlineUpdateRequest {
    
    private Long id;
    
    @NotEmpty
    private List<ChapterSummary> chapters;
}
