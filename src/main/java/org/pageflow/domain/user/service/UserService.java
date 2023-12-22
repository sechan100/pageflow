package org.pageflow.domain.user.service;

import org.pageflow.domain.user.constants.ProviderType;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.model.dto.SignupForm;

/**
 * @author : sechan
 */
public interface UserService {
    Account signup(SignupForm form, ProviderType provider, RoleType userRole);
}
