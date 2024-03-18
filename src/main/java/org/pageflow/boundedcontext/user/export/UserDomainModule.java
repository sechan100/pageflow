package org.pageflow.boundedcontext.user.export;

import org.pageflow.boundedcontext.user.entity.Account;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

/**
 * 타 도메인과의 의존성을 줄이기 위해 User 도메인의 인터페이스를 정의한다.
 * @author : sechan
 */
public interface UserDomainModule {
    @Transactional(readOnly = true)
    void findAccountByUsername(String username, Consumer<Account> consumer);
}
