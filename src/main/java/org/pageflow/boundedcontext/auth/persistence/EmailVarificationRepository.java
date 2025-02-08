package org.pageflow.boundedcontext.auth.persistence;

import org.pageflow.boundedcontext.auth.domain.EmailVarification;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface EmailVarificationRepository extends CrudRepository<EmailVarification, UUID> {
}
