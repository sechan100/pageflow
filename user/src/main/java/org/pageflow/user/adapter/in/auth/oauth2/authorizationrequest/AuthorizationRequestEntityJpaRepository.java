package org.pageflow.user.adapter.in.auth.oauth2.authorizationrequest;

import org.springframework.data.repository.CrudRepository;

/**
 * @author : sechan
 */
public interface AuthorizationRequestEntityJpaRepository extends CrudRepository<OAuth2AUthorizationRequestEntity, String> {

}
