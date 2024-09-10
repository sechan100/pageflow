package org.pageflow.global.web;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.pageflow.shared.type.TSID;

import java.io.IOException;

/**
 * @author : sechan
 */
public class TSIDDeserializer extends JsonDeserializer<TSID> {

    @Override
    public TSID deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        try {
            return TSID.from(p.getValueAsString());
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }


}