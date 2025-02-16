package org.pageflow.common.shared.type;

import java.io.Serializable;

/**
 * children class must not use Lombok's @ToString, @EqualsAndHashcode;
 * lombok의 ToString을 자식에서 사용하게되면, 올바른 문자열 변환이 이루어지지 않게됨.
 *
 * @param <V> ValueType: 해당 타입은 반드시 올바른 toString, equals, hashCode 메소드를 구현해야한다.
 * @author : sechan
 */
public abstract class SingleValueWrapper<V> implements Serializable {
  protected final V value;

  public SingleValueWrapper(V value) {
    this.value = value;
  }

  public V getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj==null) return false;
    if(obj==this) return true;
    if(obj.getClass()!=this.getClass()) return false;
    SingleValueWrapper<?> other = (SingleValueWrapper<?>) obj;
    return this.value.equals(other.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }
}
