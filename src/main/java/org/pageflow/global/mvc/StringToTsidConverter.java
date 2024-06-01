package org.pageflow.global.mvc;

import org.pageflow.shared.type.TSID;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * @author : sechan
 */
@Component
public class StringToTsidConverter implements Converter<String, TSID> {
    @Nullable
    @Override
    public TSID convert(String source) {
        if (source.isEmpty()) {
            return null;
        }
        try {
            return TSID.from(source);
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("문자열을 TSID로 converting 실패" + source, e);
        }
    }
}
