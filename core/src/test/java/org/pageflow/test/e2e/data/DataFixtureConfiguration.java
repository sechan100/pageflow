package org.pageflow.test.e2e.data;

import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author : sechan
 */
@RequiredArgsConstructor
public class DataFixtureConfiguration {
  private final List<DataFixture> dataFixtures;


  /**
   * 데이터 픽스처를 설정합니다.
   * @precondition 데이터베이스가 비어있는 상태여야한다.
   */
  public void configure() {
    dataFixtures.forEach(DataFixture::configure);
  }

}
