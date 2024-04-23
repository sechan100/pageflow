package org.pageflow.shared.type;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * @author : sechan
 */
public class TSIDSerializer extends StdSerializer<TSID> {

    public TSIDSerializer() {
        super(TSID.class);
    }

    @Override
    public void serialize(TSID value, JsonGenerator gen, SerializerProvider provider) {
        try {
            gen.writeNumber(value.toLong());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}