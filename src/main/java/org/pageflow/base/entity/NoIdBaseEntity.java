package org.pageflow.base.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


/**
 * Date, time, datetime과 같은 데이터형들은 정확히 date만을 나타내는지, time만을 나타내는지, 아니면 date와 time을 모두 나타내는지를 명확히 알기 어렵다.
 * 때문이 이름에서 이를 표현하기 위해서 명시적으로 Datetime을 이름에 포함시킨다.
 */
@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public abstract class NoIdBaseEntity implements BaseEntity {
    
    @CreatedDate
    private LocalDateTime createdDatetime;
    
    @LastModifiedDate
    private LocalDateTime modifiedDatetime;

}