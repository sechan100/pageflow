package org.pageflow.boundedcontext.user.repository;

import org.pageflow.boundedcontext.user.entity.EmailVerificationRequest;
import org.springframework.data.repository.CrudRepository;

/**
 * @author : sechan
 */
public interface EmailVerificationRequestRepository extends CrudRepository<EmailVerificationRequest, String> {
    EmailVerificationRequest findByEmail(String email);
}
