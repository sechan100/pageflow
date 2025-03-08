package org.pageflow.user.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.validation.FieldValidationResult;
import org.pageflow.common.validation.FieldValidator;
import org.pageflow.user.port.out.LoadForbiddenWordPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PennameValidator {
  public static final int MIN_LENGTH = 1;
  public static final int MAX_LENGTH = 20;
  public static final String REGEX = "^[가-힣a-zA-Z0-9]{" + MIN_LENGTH + "," + MAX_LENGTH + "}$";
  public static final String REGEX_MESSAGE= "필명은 한글, 영문, 숫자만 입력할 수 있습니다.";
  private static final Collection<String> INVALID_PENNAME = Set.of(
    "admin", "administrator", "anonymous", "pageflow", "관리자", "어드민", "페이지플로우", "매니저", "고객센터", "운영자"
  );

  private final LoadForbiddenWordPort loadForbiddenWordPort;


  public FieldValidationResult validate(String penname) {
    Collection<String> forbiddenWords = loadForbiddenWordPort.loadPennameForbiddenWords();

    FieldValidator<String> validator = new FieldValidator<>("penname", penname)
      .minLength(MIN_LENGTH, String.format("필명은 최소 %d자 이상 입력해야 합니다.", MIN_LENGTH))
      .maxLength(MAX_LENGTH, String.format("필명은 최대 %d자 이하로 입력해야 합니다.", MAX_LENGTH))
      .regex(REGEX, REGEX_MESSAGE)
      .notSame(INVALID_PENNAME, w -> String.format("필명에 '%s'은(는) 사용할 수 없습니다.", w))
      .notContains(forbiddenWords, w -> String.format("필명에 '%s'은(는) 사용할 수 없습니다.", w));

    return validator.validate();
  }
}
