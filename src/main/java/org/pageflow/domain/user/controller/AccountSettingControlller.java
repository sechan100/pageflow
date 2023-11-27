package org.pageflow.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.pageflow.domain.user.entity.Profile;
import org.pageflow.domain.user.model.dto.*;
import org.pageflow.domain.user.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@Transactional
public class AccountSettingControlller {

    private final Rq rq;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/account/settings")
    public String accountSettings(Model model) {

        model.addAttribute("account", rq.getAccount());

        return "/user/account/settings";
    }
    
    
    @PutMapping("/api/account/settings/profile")
    public ResponseEntity<Profile> updateProfile(@ModelAttribute ProfileUpdateForm form) {
        
        // TODO: 2021-08-17 1. form validation
        if(!Objects.equals(rq.getAccount().getProfile().getId(), form.getId())) {
            return ResponseEntity.status(403).build();
        }
        
        Profile profile = accountService.updateProfile(form);
        
        // 사용자 세션정보 초기화
        ((PrincipalContext)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).setUserSession(new UserSession(profile));
        
        return ResponseEntity.ok(profile);
    }
    
    @PutMapping("/api/account/settings/password")
    public ResponseEntity<Void> updatePassword(@RequestParam Map<String, String> form) {
        // form: { currentPassword: String, newPassword: String, newPasswordConfirm: String }
        
        String currentPassword = rq.getAccount().getPassword();
        
        // 기존 비밀번호 불일치
        if(!passwordEncoder.matches(form.get("currentPassword"), currentPassword)) {
            return ResponseEntity.status(403).build();
        }
        
        // 새 비밀번호 불일치
        if(!Objects.equals(form.get("newPassword"), form.get("newPasswordConfirm"))) {
            return ResponseEntity.badRequest().build();
        }
        
        accountService.updateAccount(
                AccoutUpdateForm.builder()
                        .username(rq.getAccount().getUsername())
                        .password(form.get("newPassword"))
                        .build()
        );
        
        return ResponseEntity.ok().build();
    }

}















