package org.pageflow.boundedcontext.user.adapter.out.cache.repository;

import org.pageflow.boundedcontext.user.adapter.out.cache.entity.EmailVerificationRequest;
import org.springframework.data.repository.CrudRepository;

/**
 * @author : sechan
 */
public interface EmailVerificationRequestRedisRepo extends CrudRepository<EmailVerificationRequest, Long> {
}
