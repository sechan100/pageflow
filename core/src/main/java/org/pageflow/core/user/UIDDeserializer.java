package org.pageflow.core.user;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.pageflow.common.user.UID;

/**
 * @author : sechan
 */
public class UIDDeserializer extends JsonDeserializer<UID> {

  @Override
  public UID deserialize(JsonParser p, DeserializationContext ctxt) {
    try {
      return UID.from(p.getValueAsString());
    } catch(Exception e) {
      throw new IllegalArgumentException(e);
    }
  }


}