package org.pageflow.user.adapter.in.auth.oauth2.presignup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.user.adapter.in.auth.oauth2.owner.OAuth2ResourceOwner;
import org.pageflow.user.adapter.out.OAuth2PresignupPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OAuth2PreSignupService {
  private final OAuth2PresignupPersistencePort outPort;

  public PreSignupDto preSignup(OAuth2ResourceOwner owner){
    Optional<OAuth2PreSignup> preSignup = outPort.findById(owner.getUsername());

    if(preSignup.isEmpty()){
      var newPreSignup = OAuth2PreSignup.of(
        owner.getUsername(),
        owner.getProviderType(),
        owner.getProfileImgUrl()
      );
      outPort.save(newPreSignup);
      return PreSignupDto.from(newPreSignup);
    } else {
      return PreSignupDto.from(preSignup.get());
    }
  }

  public Optional<PreSignupDto> loadAndRemove(String username){
    Optional<OAuth2PreSignup> preSignup = outPort.findById(username);
    preSignup.ifPresent(outPort::delete);
    return preSignup.map(PreSignupDto::from);
  }

}
