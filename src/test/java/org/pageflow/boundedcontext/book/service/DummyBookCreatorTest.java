package org.pageflow.boundedcontext.book.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : sechan
 */
@SpringBootTest
class DummyBookCreatorTest {

    @Autowired
    private DummyBookCreator dummyBookCreator;

    @Test
    void createDummy(){
        dummyBookCreator.createDummy();
    }
}