package org.pageflow.domain.book.model.outline;

import lombok.Builder;
import lombok.Data;
import org.pageflow.domain.user.entity.Profile;

import java.util.List;

@Data
@Builder
public class Outline {
    
    private Long id;
    private String title;
    private String coverImgUrl;
    private boolean published;
    private Profile author;
    private List<ChapterSummary> chapters;
    
}
