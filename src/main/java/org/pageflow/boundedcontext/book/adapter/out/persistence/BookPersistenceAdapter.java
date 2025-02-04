package org.pageflow.boundedcontext.book.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.BookJpaEntity;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.BookJpaRepository;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.FolderJpaEntity;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.FolderJpaRepository;
import org.pageflow.boundedcontext.book.domain.*;
import org.pageflow.boundedcontext.book.port.in.BookCreateCmd;
import org.pageflow.boundedcontext.book.port.out.BookPersistencePort;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.adapter.out.persistence.jpa.ProfileJpaEntity;
import org.pageflow.boundedcontext.user.adapter.out.persistence.jpa.ProfileJpaRepository;
import org.pageflow.boundedcontext.user.domain.Penname;
import org.pageflow.shared.type.TSID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author : sechan
 */
@Service
@Transactional
@RequiredArgsConstructor
public class BookPersistenceAdapter implements BookPersistencePort {
    private final BookJpaRepository bookRepo;
    private final FolderJpaRepository folderRepo;
    private final ProfileJpaRepository profileRepo;

    @Override
    public Book createBook(BookCreateCmd cmd) {
        // 유저
        ProfileJpaEntity profile = profileRepo.getReferenceById(cmd.getAuthorId().toLong());

        // 책
        Long bookId = TSID.Factory.getTsid().toLong();
        BookJpaEntity book = BookJpaEntity.builder()
            .id(bookId)
            .author(profile)
            .title(cmd.getTitle().getValue())
            .coverImageUrl(cmd.getCoverImageUrl().getValue())
            .build();
        bookRepo.persist(book);

        // root folder
        FolderJpaEntity rootFolder = new FolderJpaEntity(
            TSID.Factory.getTsid().toLong(),
            book,
            ":root",
            null,
            0
        );
        folderRepo.persist(rootFolder);
        return toDomain(book);
    }

    @Override
    public Optional<Book> loadBook(BookId id) {
        return bookRepo.findById(id.toLong())
            .map(this::toDomain);
    }

    @Override
    public void deleteBook(BookId id) {
        bookRepo.deleteById(id.toLong());
    }

    @Override
    public Book saveBook(Book book) {
        BookJpaEntity entity = bookRepo.findById(book.getId().toLong()).get();
        entity.setTitle(book.getTitle().getValue());
        entity.setCoverImageUrl(book.getCoverImageUrl().getValue());
        return book;
    }

    private Book toDomain(BookJpaEntity entity) {
        ProfileJpaEntity authorEntity = entity.getAuthor();
        Author author = new Author(
            UID.from(authorEntity.getId()),
            Penname.from(authorEntity.getPenname())
        );
        return new Book(
            BookId.from(entity.getId()),
            author,
            Title.from(entity.getTitle()),
            CoverImageUrl.from(entity.getCoverImageUrl())
        );
    }
}
