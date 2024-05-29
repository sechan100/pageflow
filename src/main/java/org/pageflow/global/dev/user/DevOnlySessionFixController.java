package org.pageflow.global.dev.user;

import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.domain.Account;
import org.pageflow.boundedcontext.auth.port.out.AccountPersistencePort;
import org.pageflow.global.api.RequestContext;
import org.pageflow.global.filter.DevOnlyJwtSessionFixFilter;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 토큰 기반으로 동작하는 세션은 테스트에 불편함이 많음.
 * 해당 컨트롤러를 통해서 아예 서버수준에서 현재 세션 사용자를 고정시켜버림
 * @author : sechan
 */
@Profile("dev")
@RestController
@RequiredArgsConstructor
public class DevOnlySessionFixController {
    private final RequestContext rqctx;
    private final AccountPersistencePort accountPersistencePort;
    private final DevOnlyJwtSessionFixFilter devOnlyJwtSessionFixFilter;

    @PostMapping("/DEV_ONLY/session/fix")
    @Operation(summary = "서버수준에서 현재 세션 사용자를 고정")
    public void fixSession(@RequestBody Req.SessionPrincipal req) {
        Account account = accountPersistencePort.loadAccount(req.username).get();
        devOnlyJwtSessionFixFilter.fixSession(account.getUid(), account.getRole());
    }

    @DeleteMapping("/DEV_ONLY/session/fix")
    @Operation(summary = "서버수준에서 현재 세션 사용자 고정 해제")
    public void unfixSession() {
        devOnlyJwtSessionFixFilter.clearFixedPrincipal();
    }


    public static class Req {
        @Data
        public static class SessionPrincipal {
            private String username;
        }
    }
}
