package org.pageflow.global.web;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.pageflow.shared.type.TSID;

/**
 * @author : sechan
 */
public class TSIDSerializer extends JsonSerializer<TSID> {

  @Override
  public void serialize(TSID value, JsonGenerator gen, SerializerProvider provider) {
    try {
      gen.writeString(value.toString());
    } catch(Exception e){
      throw new IllegalArgumentException(e);
    }
  }


}