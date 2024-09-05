package org.pageflow.tsid;

import org.junit.jupiter.api.Test;
import org.pageflow.shared.type.TSID;
import support.TestOnlyTsidFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author : sechan
 */
class TestOnlyTsidFactoryFactoryTest {
    private static final int SEED = 1;

    @Test
    void create() throws InterruptedException {
        TestOnlyTsidFactory f1 = new TestOnlyTsidFactory(SEED);
        TestOnlyTsidFactory f2 = new TestOnlyTsidFactory(SEED);

        TSID id1 = f1.generate();
        Thread.sleep(500);
        TSID id2 = f2.generate();
        assertEquals(id1, id2, "같은 factory로 생성된 id가 다름");
    }

}