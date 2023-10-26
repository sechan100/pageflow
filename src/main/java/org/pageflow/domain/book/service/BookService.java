package org.pageflow.domain.book.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.entity.Page;
import org.pageflow.domain.book.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
@RequiredArgsConstructor
// final 필드들 매개변수로 받는 생성자 자동 생성
// spring에서 편의로 생성자가 딱 하나 일때는 @Auto 안 붙여도 자동으로 붙여줌
public class BookService {

    private final BookRepository bookRepository;

    public List<Book> getList() {
        return this.bookRepository.findAll();
    }
}
