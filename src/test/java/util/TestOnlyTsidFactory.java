package util;

import org.pageflow.shared.type.TSID;

import java.util.Random;

/**
 * 테스트에 사용하는 TSID 생성하는 팩토리
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