package org.pageflow.global.api;

import lombok.Getter;
import lombok.Value;
import org.pageflow.boundedcontext.user.domain.Password;
import org.pageflow.boundedcontext.user.domain.Penname;

import static lombok.AccessLevel.NONE;

/**
 * @author : sechan
 */
@Value
public class FeedbackTemplate {
    @Getter
    private static final FeedbackTemplate INSTANCE = new FeedbackTemplate();

    // 피드백을 줄 수 없는 경우
    String canNotFeedback = "요청 처리중 오류가 발생했습니다. 관리자에게 문의해주세요.";

    // username
    String username_regexMismatch = Password.REGEX_DESCRIPTION;
    String username_duplicate = "이미 사용중인 아이디입니다. 다른 아이디를 입력해주세요.";
    @Getter(NONE)String username_containsForbiddenWord = "'%s'은(는) 아이디에 포함될 수 없습니다. 다른 아이디를 입력해주세요.";

    // password
    String password_regexMismatch = Password.REGEX_DESCRIPTION;
    String password_notMatch = "비밀번호가 일치하지 않습니다. 다시 입력해주세요.";

    // email
    String email_duplicate = "해당 이메일은 이미 사용중입니다. 다른 이메일을 입력해주세요.";
    String email_RegexMismatch = "이메일 형식이 올바르지 않습니다. 올바른 이메일을 입력해주세요.";

    // penname
    String penname_RegexMismatch = Penname.REGEX_DESCRIPTION;
    @Getter(NONE)String penname_containsForbiddenWord = "'%s'은(는) 필명에 포함될 수 없습니다. 다른 필명을 입력해주세요.";





    // getters
    public String getUsername_containsForbiddenWord(String forbiddenWord) {
        return String.format(username_containsForbiddenWord, forbiddenWord);
    }
    public String getPenname_containsForbiddenWord(String forbiddenWord) {
        return String.format(penname_containsForbiddenWord, forbiddenWord);
    }

}
