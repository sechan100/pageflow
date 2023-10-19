package org.pageflow.boundedcontext.user.repository;

import io.sseg.boundedcontext.user.model.dto.AwaitingEmailVerifyingRedisEntity;
import org.springframework.data.repository.CrudRepository;

public interface AwaitingEmailVerifyingFormRepository extends CrudRepository<AwaitingEmailVerifyingRedisEntity, String> {

}
