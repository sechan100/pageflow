package org.pageflow.domain.user.constants;

import lombok.Getter;

import java.util.Set;

public class UserSignupPolicy {

    public static class Constraints {

        @Getter
        private final static Set<String> invalidUsernames = Set.of("admin", "anonymous");

    }

    public static class ViolationType {
        public static final String USERNAME_DUPLICATION = "이미 사용중인 아이디입니다.";
        public static final String EMAIL_DUPLICATION = "이미 사용중인 이메일입니다.";
        public static final String INVALID_EMAIL = "유효하지 않은 이메일입니다.";
        public static final String INVALID_USERNAME = "사용할 수 없는 아이디입니다.";
        public static final String INVALID_PASSWORD = "사용할 수 없는 비밀번호입니다.";
        public static final String INVALID_PASSWORD_CONFIRM = "비밀번호 확인이 일치하지 않습니다.";
        public static final String INVALID_PROVIDER = "유효하지 않은 소셜 로그인입니다.";
    }
}
