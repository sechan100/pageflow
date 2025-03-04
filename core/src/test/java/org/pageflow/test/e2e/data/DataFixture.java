package org.pageflow.test.e2e.data;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * api로 요청을 보내서 데이터를 생성해야한다.
 * @author : sechan
 */
@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface DataFixture {
  void configure();
}
