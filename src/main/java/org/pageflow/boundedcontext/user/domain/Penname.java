package org.pageflow.boundedcontext.user.domain;

import org.pageflow.global.api.code.Code4;
import org.pageflow.shared.type.SingleValueWrapper;

/**
 * @author : sechan
 */
public class Penname extends SingleValueWrapper<String> {
    public static final int MIN_LENGTH = 1;
    public static final int MAX_LENGTH = 12;
    public static final String REGEX = "^[가-힣a-zA-Z0-9]{" + MIN_LENGTH + "," + MAX_LENGTH + "}$";
    public static final String REGEX_DESCRIPTION = String.format(
        "필명은 %d ~ %d자 사이의 한글, 영문, 숫자로 입력해주세요.", MIN_LENGTH, MAX_LENGTH
    );

    private Penname(String value) {
        super(value);
    }

    public static Penname of(String value) {
        validate(value);
        return new Penname(value);
    }

    private static void validate(String penname) {
        if(penname == null || penname.isEmpty()){
            throw Code4.EMPTY_VALUE.feedback("필명을 입력해주세요.");
        }
        if(!penname.matches(REGEX)){
            throw Code4.FORMAT_MISMATCH.feedback(t -> t.getPenname_RegexMismatch());
        }
    }


}
