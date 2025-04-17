package org.pageflow.book.domain.toc.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.common.jpa.BaseJpaEntity;

import java.util.UUID;

/**
 * @author : sechan
 */
@Entity
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class SectionDetails extends BaseJpaEntity {
  private static final boolean DEFAULT_SHOULD_SHOW_TITLE = true;
  private static final boolean DEFAULT_SHOULD_BREAK_SECTION = false;

  @Id
  @Getter
  private UUID id;

  @Setter
  @Getter
  @OneToOne(
    fetch = FetchType.LAZY,
    cascade = CascadeType.ALL
  )
  @JoinColumn(name = "content_id")
  private SectionContent content;

  /**
   * 섹션이 시작할 때, 섹션 제목을 보여줄 것인지 여부
   */
  @Getter
  @Setter
  @Column(nullable = false)
  private boolean shouldShowTitle;

  /**
   * 섹션을 보일 때, 다음 페이지에 끊어서 렌더링을 시작해야하는지 여부
   */
  @Getter
  @Setter
  @Column(nullable = false)
  private boolean shouldBreakSection;


  public static SectionDetails create() {
    return new SectionDetails(
      UUID.randomUUID(),
      SectionContent.create(),
      DEFAULT_SHOULD_SHOW_TITLE,
      DEFAULT_SHOULD_BREAK_SECTION
    );
  }

}
