package org.pageflow.boundedcontext.book.port.out;

import org.pageflow.boundedcontext.book.domain.Book;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.port.in.BookCreateCmd;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface BookPersistencePort {
    Book createBook(BookCreateCmd cmd);
    Optional<Book> loadBook(BookId id);
    void deleteBook(BookId id);
    Book saveBook(Book book);
}
