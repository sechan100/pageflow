package org.pageflow.boundedcontext.user.repository;

import io.hypersistence.tsid.TSID;
import org.pageflow.boundedcontext.user.entity.EmailVerificationRequest;
import org.springframework.data.repository.CrudRepository;

/**
 * @author : sechan
 */
public interface EmailVerificationRequestRepo extends CrudRepository<EmailVerificationRequest, TSID> {
}
