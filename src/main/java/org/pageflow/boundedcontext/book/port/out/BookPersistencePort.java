package org.pageflow.boundedcontext.book.port.out;

import org.pageflow.boundedcontext.book.domain.Book;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.port.in.CreateBookCmd;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface BookPersistencePort {
    Book createBook(CreateBookCmd cmd);
    Optional<Book> loadBook(BookId id);
    void deleteBook(BookId id);
    Book saveBook(Book book);
}
