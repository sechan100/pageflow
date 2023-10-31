package org.pageflow;

import org.junit.jupiter.api.Test;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.repository.BookRepository;
import org.pageflow.domain.book.service.BookService;
import org.pageflow.domain.testbook.TestBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PageflowApplicationTests {
	@Autowired
	private TestBookService testBookService;
	@Test
	void contextLoads() {
		for (int i = 1; i <= 300; i++) {
			String title = String.format("테스트 데이터입니다:[%03d]", i);
			String content = "내용무";
			this.testBookService.create(title, content);
		}
	}
}
