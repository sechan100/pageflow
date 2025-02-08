package support;

import org.pageflow.shared.type.TSID;

import java.util.Random;

/**
 * 테스트에 사용하는 TSID 생성하는 팩토리
 * tsid가 시간에 따라 변화하기 때문에, 아무리 seed를 동일하게 넣어도 만들어지는 시간차에 따라서 tsid가 달라진다.
 *
 * @author : sechan
 */
public class TestOnlyTsidFactory {
  private final Random random;

  public TestOnlyTsidFactory(int seed) {
    this.random = new Random(seed);
  }

  public TSID generate() {
    return new TSID(random.nextLong());
  }
}