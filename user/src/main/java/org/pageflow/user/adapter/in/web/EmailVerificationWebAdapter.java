//package org.pageflow.user.adapter.in.web;
//
//import io.swagger.v3.oas.annotations.Operation;
//import lombok.RequiredArgsConstructor;
//import org.pageflow.user.port.in.EmailVerificationUseCase;
//import org.pageflow.boundedcontext.common.value.UID;
//import org.pageflow.global.api.ApiAccess;
//import org.pageflow.global.api.RequestContext;
//import org.pageflow.global.property.AppProps;
//import org.springframework.security.access.annotation.Secured;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import java.util.UUID;
//
///**
// * @author : sechan
// */
//@Controller
//@RequiredArgsConstructor
//public class EmailVerificationWebAdapter {
//  private final AppProps props;
//  private final RequestContext requestContext;
//  private final EmailVerificationUseCase useCase;
//
//
//  @Secured(ApiAccess.USER)
//  @Operation(summary = "이메일 인증요청 전송", description = "로그인 중인 사용자의 이메일로 인증메일을 전송한다.")
//  @PostMapping("/auth/email/send-verification-email")
//  @ResponseBody
//  public void sendVerificationEmail() {
//    UID uid = UID.from(requestContext.getUid());
//    useCase.sendVerificationEmail(uid);
//  }
//
//  public static final String EMAIL_VERIFICATION_URI = "/auth/email/verify";
//
//  @RequestMapping(EMAIL_VERIFICATION_URI)
//  public String verifyEmail(String uid, String authCode, Model model) {
//    useCase.verify(UID.from(uid), UUID.fromString(authCode));
//    model.addAttribute("clientUrl", props.site.clientUrl);
//    return "/user/email-verification-success";
//  }
//}
