package org.pageflow.infra.file.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.pageflow.base.entity.BaseEntity;
import org.pageflow.infra.file.constants.FileMetadataType;

@Entity
@SuperBuilder //상속구조에서 사용. 기반 클래스의 필드까지 포함하는 빌더 생성
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "file_metadata",
        indexes = @Index(name = "idx_file_metadata_owner_and_file_metadata_type", columnList = "owner_id, owner_entity_type, file_metadata_type")
)
public class FileMetadata extends BaseEntity { //메타데이터 저장.
    
    @Column(name = "owner_id")
    private Long ownerId;
    
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
     * {uploadDirectory}/
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
    private String managedFilename;
    
    /**
     * 확장자
     */
    private String originalExtension;
    
    /**
     * {y}/{m}/{d}/
     */
    private String pathPrefix;

}

