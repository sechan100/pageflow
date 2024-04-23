package org.pageflow.boundedcontext.file.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.boundedcontext.file.model.FilePath;
import org.pageflow.boundedcontext.file.shared.FileOwnerType;
import org.pageflow.boundedcontext.file.shared.FileType;
import org.pageflow.shared.jpa.BaseJpaEntity;

@Data
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "managedFilename", callSuper = false)
@Table(
    name = "file_data",
    indexes = { @Index(name = "idx_owner_identity", columnList = "owner_id, owner_type"),}
)
public class FileData extends BaseJpaEntity {

    @Id
    private String managedFilename; // UUID

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

    @Column(name = "owner_id")
    private Long ownerId; // tsid

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type")
    private FileOwnerType ownerType;

    /**
     * Owner 객체가 여러개의 파일을 가질 수 있는 경우를 고려하여 저장.
     * ex) FileType이 IMG인 경우, 사용자가 프로필 이미지와 배경 이미지를 모두 가지는 경우, 파일 특정이 불가능해진다.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "file_type")
    private FileType fileType;


    public FilePath toStaticPath() {
        return new FilePath(staticParent, managedFilename, extension);
    }

}

