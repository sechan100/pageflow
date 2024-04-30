package org.pageflow.boundedcontext.file.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.boundedcontext.file.model.FilePath;
import org.pageflow.boundedcontext.file.shared.FileOwnerType;
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

    /*
     * enum 타입이지만, 인터페이스 구현체인 enum 타입이라서 @Enumerated를 사용할 수가 없음
     **/
    @Column(name = "file_type")
    private String fileType;


    public FilePath toStaticPath() {
        return new FilePath(staticParent, managedFilename, extension);
    }

}

