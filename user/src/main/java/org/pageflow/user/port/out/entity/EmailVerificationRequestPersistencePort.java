package org.pageflow.user.port.out.entity;

import org.pageflow.user.domain.entity.EmailVerificationRequest;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface EmailVerificationRequestPersistencePort extends CrudRepository<EmailVerificationRequest, UUID> {
}
