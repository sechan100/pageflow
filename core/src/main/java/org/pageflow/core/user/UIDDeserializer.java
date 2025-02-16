package org.pageflow.core.user;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.pageflow.common.user.UID;

import java.io.IOException;

/**
 * @author : sechan
 */
public class UIDDeserializer extends JsonDeserializer<UID> {

  @Override
  public UID deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
    try {
      return UID.from(p.getValueAsString());
    } catch(Exception e){
      throw new IllegalArgumentException(e);
    }
  }


}