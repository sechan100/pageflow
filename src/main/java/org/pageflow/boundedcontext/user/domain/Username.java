package org.pageflow.boundedcontext.user.domain;

import org.pageflow.global.api.code.Code4;
import org.pageflow.shared.type.SingleValueWrapper;

import java.util.regex.Pattern;

/**
 * @author : sechan
 */
public class Username extends SingleValueWrapper<String> {
    public static final int MIN_LENGTH = 4;
    public static final int MAX_LENGTH = 100;
    private static final Pattern REGEX = Pattern.compile("^[a-zA-Z0-9-]{" + MIN_LENGTH + "," + MAX_LENGTH + "}$");
    public static final String REGEX_DESCRIPTION = String.format(
        "아이디는 영문, 숫자만으로 이루어진 %d ~ %d 사이의 문자여야 합니다. 아이디를 다시 확인해주세요."
        , MIN_LENGTH, MAX_LENGTH
    );



    private Username(String value){
        super(value);
    }

    public static Username of(String value){
        validate(value);
        return new Username(value);
    }


    private static void validate(String username){
        assert username != null;

        // 정규식 검사
        if(!REGEX.matcher(username).matches()){
            throw Code4.FORMAT_MISMATCH.feedback(t -> t.getUsername_regexMismatch());
        }
    }
}
