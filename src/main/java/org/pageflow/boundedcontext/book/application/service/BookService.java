package org.pageflow.boundedcontext.book.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.boundedcontext.book.application.dto.BookDto;
import org.pageflow.boundedcontext.book.domain.Book;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.CoverImageUrl;
import org.pageflow.boundedcontext.book.domain.Title;
import org.pageflow.boundedcontext.book.port.in.BookUseCase;
import org.pageflow.boundedcontext.book.port.in.CreateBookCmd;
import org.pageflow.boundedcontext.book.port.out.BookPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookService implements BookUseCase {
    private final BookPersistencePort persistPort;


    @Override
    public BookDto.Simple createBook(CreateBookCmd cmd) {
        Book book = persistPort.createBook(cmd);
        return toDto(book);
    }

    @Override
    public BookDto.Simple changeBookTitle(BookId id, Title title) {
        Book book = persistPort.loadBook(id).get();
        book.changeTitle(title);
        persistPort.saveBook(book);
        return toDto(book);
    }

    @Override
    public BookDto.Simple changeBookCoverImage(BookId id, CoverImageUrl url) {
        Book book = persistPort.loadBook(id).get();
        book.changeCoverImageUrl(url);
        persistPort.saveBook(book);
        return toDto(book);
    }

    private BookDto.Simple toDto(Book book) {
        return new BookDto.Simple(
            book.getId().getValue(),
            book.getTitle().getValue(),
            book.getCoverImageUrl().getValue()
        );
    }
}
