package org.pageflow.global.dev;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.domain.Account;
import org.pageflow.boundedcontext.auth.port.out.AccountPersistencePort;
import org.pageflow.global.api.ApiAccess;
import org.pageflow.global.api.RequestContext;
import org.pageflow.global.filter.DevOnlyJwtSessionFixFilter;
import org.pageflow.shared.annotation.Post;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 토큰 기반으로 동작하는 세션은 테스트에 불편함이 많다. 특히 클라이언트 앱을 이용하지 않고 api만을 가지고 진행하는 테스트에서 그렇다.
 * 해당 컨트롤러는 '서버수준'에서 현재 세션 사용자를 고정시키거나 해제하기 위한 인터페이스를 제공한다.
 * @author : sechan
 */
@Profile("dev")
@RestController
@RequiredArgsConstructor
public class SessionFixController {
    private final RequestContext rqctx;
    private final AccountPersistencePort accountPersistencePort;
    private final DevOnlyJwtSessionFixFilter devOnlyJwtSessionFixFilter;

    @Post(value = "/DEV_ONLY/session/fix", access = ApiAccess.ANONYMOUS)
    @Operation(summary = "서버수준에서 현재 세션 사용자를 고정 또는 해제")
    public void fixSession(
        @RequestBody Req.SessionPrincipal req,
        @RequestParam(defaultValue = "false") Boolean unfix
    ) {
        // 세션 고정
        if(!unfix){
            Account account = accountPersistencePort.loadAccount(req.username).get();
            devOnlyJwtSessionFixFilter.fixSession(account.getUid(), account.getRole());
        } else {
            devOnlyJwtSessionFixFilter.clearFixedPrincipal();
        }
    }

    public static class Req {
        @Data
        public static class SessionPrincipal {
            @Schema(description = "username", defaultValue = "user1")
            private String username;
        }
    }
}
