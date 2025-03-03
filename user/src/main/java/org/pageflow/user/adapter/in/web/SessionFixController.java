package org.pageflow.user.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.api.RequestContext;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 토큰 기반으로 동작하는 세션은 테스트에 불편함이 많다. 특히 클라이언트 앱을 이용하지 않고 api만을 가지고 진행하는 테스트에서 그렇다.
 * 해당 컨트롤러는 '서버수준'에서 현재 세션 사용자를 고정시키거나 해제하기 위한 인터페이스를 제공한다.
 *
 * @author : sechan
 */
@Profile("dev")
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Session Fix", description = "서버수준에서 현재 세션 사용자를 고정 또는 해제")
public class SessionFixController {
  private final RequestContext rqctx;
  private final JwtSessionFixer sessionFixer;

  @PostMapping(value = "/DEV_ONLY/session/fix")
  @Operation(summary = "서버수준에서 현재 세션 사용자를 고정 또는 해제")
  public void fixSession(
    @RequestBody Username req,
    @RequestParam(defaultValue = "false") Boolean unfix
  ) {
    // 세션 고정
    if(!unfix){
      sessionFixer.fixSession(req.getUsername());
    } else {
      sessionFixer.clearFixedPrincipal();
    }
  }

  @Data
  public static class Username {
    @NotBlank
    @Schema(description = "username", defaultValue = "user1")
    private String username;
  }

}
