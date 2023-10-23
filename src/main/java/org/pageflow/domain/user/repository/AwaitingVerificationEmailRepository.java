package org.pageflow.domain.user.repository;


import org.pageflow.domain.user.entity.AwaitingEmailVerificationRequest;
import org.springframework.data.repository.CrudRepository;

public interface AwaitingVerificationEmailRepository extends CrudRepository<AwaitingEmailVerificationRequest, String> {

}
