package org.pageflow.file.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.common.jpa.BaseJpaEntity;
import org.pageflow.file.model.FilePath;
import org.pageflow.file.shared.FileType;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "filename", callSuper = false)
@Table(
  name = "file_data",
  indexes = {@Index(name = "idx_owner_file_type", columnList = "owner_id, file_type"),}
)
public class FileData extends BaseJpaEntity {

  @Id
  private UUID filename;

  // /{YYYY}/{MM}/{DD}
  @Column(name = "static_parent")
  private String staticParent;

  @Column(name = "original_filename")
  private String originalFilename;

  @Column(name = "extension")
  private String extension;

  // in bytes
  @Column(name = "size")
  private Long size;

  // 파일을 소유한 리소스 엔티티의 id
  @Column(name = "owner_id")
  private String ownerId;

  @Enumerated(EnumType.STRING)
  @Column(name = "file_type")
  private FileType fileType;


  public FilePath getFilePath() {
    return new FilePath(staticParent, filename, extension);
  }

}

