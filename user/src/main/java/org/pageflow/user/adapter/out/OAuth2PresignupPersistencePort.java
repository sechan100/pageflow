package org.pageflow.user.adapter.out;

import org.pageflow.user.adapter.in.auth.oauth2.presignup.OAuth2PreSignup;
import org.springframework.data.repository.CrudRepository;

/**
 * @author : sechan
 */
public interface OAuth2PresignupPersistencePort extends CrudRepository<OAuth2PreSignup, String> {

}
