package org.pageflow.user.port.out.entity;

import org.pageflow.common.jpa.repository.BaseJpaRepository;
import org.pageflow.user.domain.entity.EmailVerificationRequest;

/**
 * @author : sechan
 */
public interface EmailVerificationRequestPersistencePort extends BaseJpaRepository<EmailVerificationRequest, String> {
}
