package org.pageflow.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.pageflow.domain.user.entity.Profile;
import org.pageflow.domain.user.model.dto.PrincipalContext;
import org.pageflow.domain.user.model.dto.ProfileUpdateForm;
import org.pageflow.domain.user.model.dto.UserSession;
import org.pageflow.domain.user.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class AccountSettingControlller {

    private final Rq rq;
    private final AccountService accountService;

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

}
