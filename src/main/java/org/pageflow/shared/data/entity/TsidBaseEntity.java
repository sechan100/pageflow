package org.pageflow.shared.data.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import org.pageflow.shared.data.tsid.Tsid;
import org.pageflow.shared.type.TSID;

/**
 * Long 타입의 PK를 가지는 엔티티의 추상 클래스
 */
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
public abstract class TsidBaseEntity extends BaseEntity implements Entity, TsidIdentifiable {

    @Id
    @Tsid
    @Getter
    @Setter(AccessLevel.NONE)
    @Column(updatable = false, nullable = false)
    private TSID id;

}

