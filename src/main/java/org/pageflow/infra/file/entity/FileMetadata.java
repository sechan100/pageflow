package org.pageflow.infra.file.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.global.data.LongIdPkBaseBaseEntity;
import org.pageflow.infra.file.constants.FileMetadataType;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "file_metadata",
        indexes = @Index(name = "idx_file_metadata_owner_and_file_metadata_type", columnList = "owner_id, owner_entity_type, file_metadata_type")
)
public class FileMetadata extends LongIdPkBaseBaseEntity {

    @Column(name = "owner_id")
    private String ownerId;

    @Column(name = "owner_entity_type")
    private String ownerEntityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_metadata_type")
    private FileMetadataType fileMetadataType;

    /**
     * 단위: byte
     */
    private Long size;

    /**
     * 서버 업로드 디렉토리 경로
     * /{uploadDirectory}
     */
    @Column
    private String uploadDirectory;

    /**
     * {원본 파일명}.{확장자}
     */
    private String originalFilename;

    /**
     * {UUID}.{확장자}
     */
    @Column(unique = true)
    private String managedFilename;

    /**
     * 확장자
     */
    private String originalExtension;

    /**
     * /{y}/{m}/{d}
     */
    private String pathPrefix;

}

