package org.pageflow.domain.book.model.summary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pageflow.domain.book.constants.BookStatus;
import org.pageflow.domain.interaction.model.PreferenceStatistics;
import org.pageflow.domain.user.entity.Profile;

import java.util.List;

/**
 * 무거운 전체 책 데이터를 담지 않고, 책의 개요 정보만을 담아서 전달하는 POJO DTO.
 * 해당 클래스는 Book 엔티티와 대응된다고 볼 수 있고, Chapter와 Page 엔티티는 각각 {@link ChapterSummary}, {@link PageSummary}에 대응되어 담긴다.
 *
 * @author : sechan
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Outline {

    private Long id;
    private String title;
    private String coverImgUrl;
    private BookStatus status;
    private Profile author;
    private List<ChapterSummary> chapters;
    private PreferenceStatistics preferenceStatistics;
    
}
