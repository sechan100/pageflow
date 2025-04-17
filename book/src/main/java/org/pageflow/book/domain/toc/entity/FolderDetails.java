package org.pageflow.book.domain.toc.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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
public class FolderDetails extends BaseJpaEntity {

  @Id
  @Getter
  private UUID id;

  @Getter
  @Setter
  @Enumerated(EnumType.STRING)
  private FolderDesign design;


  public static FolderDetails create() {
    return new FolderDetails(
      UUID.randomUUID(),
      FolderDesign.DEFAULT
    );
  }

}
