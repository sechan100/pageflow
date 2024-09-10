package org.pageflow.global.web;

import io.vavr.control.Try;
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
            Try<TSID> tryParseTsid =
                Try.of(() -> new TSID(Long.parseLong(source))) // 18자리 숫자일 경우
                .recover(NumberFormatException.class, e -> TSID.from(source)); // 64비트 문자열일 경우
            return tryParseTsid.get();
        } catch (RuntimeException e){
            throw new IllegalArgumentException("문자열을 TSID로 converting 실패 " + source, e);
        }
    }
}
