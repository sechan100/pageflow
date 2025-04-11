package org.pageflow.common.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseJpaEntity implements JpaEntity {

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdDatetime;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime updatedDatetime;

}
