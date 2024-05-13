package org.pageflow.boundedcontext.user.domain;

import org.pageflow.boundedcontext.common.exception.InputValueException;
import org.pageflow.shared.type.SingleValueWrapper;
import org.springframework.util.StringUtils;

/**
 * @author : sechan
 */
public class Penname extends SingleValueWrapper<String> {
    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 12;
    private static final String REGEX = "^[가-힣a-zA-Z0-9]{" + MIN_LENGTH + "," + MAX_LENGTH + "}$";
    private static final String REGEX_DESCRIPTION = String.format(
        "필명은 %d ~ %d자 사이의 한글, 영문, 숫자로 입력해주세요.", MIN_LENGTH, MAX_LENGTH
    );

    private Penname(String value) {
        super(value);
    }

    public static Penname from(String value) {
        validate(value);
        return new Penname(value);
    }

    private static void validate(String penname) {
        if(!StringUtils.hasText(penname)){
            throw InputValueException.builder()
                .message("필명을 입력해주세요.")
                .field("penname", penname)
                .build();
        }
        if(!penname.matches(REGEX)){
            throw InputValueException.builder()
                .message(REGEX_DESCRIPTION)
                .field("penname", penname)
                .build();
        }
    }


}
