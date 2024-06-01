package org.pageflow.global.mvc;

import org.pageflow.shared.type.TSID;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * @author : sechan
 */
@Component
public class LongToTsidConverter implements Converter<Long, TSID> {
    @Nullable
    @Override
    public TSID convert(Long source) {
        return new TSID(source);
    }
}
