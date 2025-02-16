package org.pageflow.core.user;

import io.vavr.control.Try;
import org.pageflow.common.user.UID;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * @author : sechan
 */
@Component
public class StringToUIDConverter implements Converter<String, UID> {
  @Nullable
  @Override
  public UID convert(String source) {
    if(source.isEmpty()){
      return null;
    }
    try {
      var tryParseUID = Try.of(() -> UID.from(source));
      return tryParseUID.get();
    } catch(RuntimeException e){
      throw new IllegalArgumentException("문자열을 UID로 converting 실패 " + source, e);
    }
  }
}
