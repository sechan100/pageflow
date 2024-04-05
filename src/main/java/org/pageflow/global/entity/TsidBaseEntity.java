package org.pageflow.global.entity;

import io.hypersistence.tsid.TSID;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import org.pageflow.shared.libs.Tsid;

/**
 * Long 타입의 PK를 가지는 엔티티의 추상 클래스
 */
@MappedSuperclass
@Getter
@Setter(AccessLevel.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
public abstract class TsidBaseEntity extends AuditingBaseEntity implements Entity, TsidIdentifiable {

    @Id
    @Tsid
    @Column(updatable = false, nullable = false)
    private Long id;


    @Override
    public TSID getId() {
        return TSID.from(id);
    }

}

