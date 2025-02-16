package org.pageflow.common.shared.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 일시적으로 저장되는 크게 중요하지 않은 데이터를 저장하는데 사용되는 Entity.
 * 사용하기 위해서, 해당 클래스를 상속받고, data 필드에 저장하고 싶은 데이터를 저장하면 된다.
 * 자식 클래스에서는 다른 칼럼을 만들어서는 안된다.
 * @author : sechan
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "data_type")
@Table(name = "temp")
public abstract class TemporaryEntity<D> extends BaseJpaEntity {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Id
  private String id;

  /**
   * JSON 형태로 데이터를 저장한다.
   */
  @Lob
  @Column(columnDefinition = "TEXT")
  @Getter(AccessLevel.NONE)
  private String data;

  /**
   * 만료시간을 기록하는 UTC 시간.
   * 일정시간마다 만료시간을 검사하여 만료된 데이터는 삭제한다.
   *
   * !주의!: 최소 만료시간은 1분 이상이다.
   * 1분 이하로 설정한 데이터는 스케쥴러 실행에 맞춰서 다음 1분위 스케쥴에서 일괄 삭제되므로 원치않은 동작이 발생할 수 있으니 주의.
   */
  @Column(name = "expired_at")
  private Long expiredAt;


  public TemporaryEntity(String id, D data, Long expiredAt) {
    this.id = id;
    this.expiredAt = expiredAt;
    try {
      this.data = objectMapper.writeValueAsString(data);
    } catch(JsonProcessingException e){
      throw new RuntimeException("Could not serialize data for 'TemporaryEntity'", e);
    }
  }

  public D getData() {
    try {
      Class<D> clazz = getDataClassType();
      return objectMapper.readValue(this.data, clazz);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Could not deserialize data for 'TemporaryEntity'", e);
    }
  }

  protected abstract Class<D> getDataClassType();


}
