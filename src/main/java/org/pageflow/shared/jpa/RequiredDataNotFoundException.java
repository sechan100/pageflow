package org.pageflow.shared.jpa;

import java.util.Arrays;

/**
 * @author : sechan
 */
public class RequiredDataNotFoundException extends RuntimeException {
    private RequiredDataNotFoundException(Object... args) {
        super("JPA 엔티티를 찾을 수 없습니다. 조회 인자: " + Arrays.toString(args));
    }

    public static RequiredDataNotFoundException of(Object args1) {
        return new RequiredDataNotFoundException(args1);
    }

    public static RequiredDataNotFoundException of(Object args1, Object args2) {
        return new RequiredDataNotFoundException(args1, args2);
    }

    public static RequiredDataNotFoundException of(Object args1, Object args2, Object args3) {
        return new RequiredDataNotFoundException(args1, args2, args3);
    }

    public static RequiredDataNotFoundException of(Object args1, Object args2, Object args3, Object args4) {
        return new RequiredDataNotFoundException(args1, args2, args3, args4);
    }

    public static RequiredDataNotFoundException of(Object args1, Object args2, Object args3, Object args4, Object args5) {
        return new RequiredDataNotFoundException(args1, args2, args3, args4, args5);
    }

    public static RequiredDataNotFoundException of(Object args1, Object args2, Object args3, Object args4, Object args5, Object args6) {
        return new RequiredDataNotFoundException(args1, args2, args3, args4, args5, args6);
    }

    public static RequiredDataNotFoundException of(Object args1, Object args2, Object args3, Object args4, Object args5, Object args6, Object args7) {
        return new RequiredDataNotFoundException(args1, args2, args3, args4, args5, args6, args7);
    }

    public static RequiredDataNotFoundException of(Object args1, Object args2, Object args3, Object args4, Object args5, Object args6, Object args7, Object args8) {
        return new RequiredDataNotFoundException(args1, args2, args3, args4, args5, args6, args7, args8);
    }
}
