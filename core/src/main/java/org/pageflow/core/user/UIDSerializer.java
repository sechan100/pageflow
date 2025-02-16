package org.pageflow.core.user;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.pageflow.common.user.UID;

/**
 * @author : sechan
 */
public class UIDSerializer extends JsonSerializer<UID> {

  @Override
  public void serialize(UID value, JsonGenerator gen, SerializerProvider provider) {
    try {
      gen.writeString(value.toString());
    } catch(Exception e){
      throw new IllegalArgumentException(e);
    }
  }


}