package org.pageflow.boundedcontext.user.dto;

import lombok.Builder;
import lombok.Data;
import org.pageflow.boundedcontext.user.constants.ProviderType;
import org.pageflow.boundedcontext.user.constants.RoleType;
import org.pageflow.boundedcontext.user.entity.AccountEntity;
import org.pageflow.shared.type.TSID;

/**
 * @author : sechan
 */
@Data
@Builder
public class AccountDto {
    private final TSID id;
    private final String username;
    private final String email;
    private final boolean emailVerified;
    private final ProviderType provider;
    private final RoleType role;

    public static AccountDto from(AccountEntity account){
        return AccountDto.builder()
            .id(account.getId())
            .username(account.getUsername())
            .email(account.getEmail())
            .emailVerified(account.isEmailVerified())
            .provider(account.getProvider())
            .role(account.getRole())
            .build();
    }
}
