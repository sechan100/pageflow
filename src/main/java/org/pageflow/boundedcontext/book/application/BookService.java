package org.pageflow.boundedcontext.book.application;

import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.*;
import org.pageflow.boundedcontext.book.domain.*;
import org.pageflow.boundedcontext.book.dto.AuthorDto;
import org.pageflow.boundedcontext.book.dto.BookDto;
import org.pageflow.boundedcontext.book.dto.FolderDto;
import org.pageflow.boundedcontext.book.dto.SectionDto;
import org.pageflow.boundedcontext.book.port.in.BookCreateCmd;
import org.pageflow.boundedcontext.book.port.in.BookQueries;
import org.pageflow.boundedcontext.book.port.in.BookUseCase;
import org.pageflow.boundedcontext.book.port.out.BookPersistencePort;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.adapter.out.persistence.jpa.ProfileJpaEntity;
import org.pageflow.shared.type.TSID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookService implements BookUseCase, BookQueries {
    private final BookPersistencePort persistPort;
    private final BookJpaRepository bookJpaRepository;
    private final NodeJpaRepository nodeJpaRepository;


    @Override
    public BookDto.Basic createBook(BookCreateCmd cmd) {
        Book book = persistPort.createBook(cmd);
        return toDto(book);
    }

    @Override
    public BookDto.Basic changeBookTitle(BookId id, Title title) {
        Book book = persistPort.loadBook(id).get();
        book.changeTitle(title);
        persistPort.saveBook(book);
        return toDto(book);
    }

    @Override
    public BookDto.Basic changeBookCoverImage(BookId id, CoverImageUrl url) {
        Book book = persistPort.loadBook(id).get();
        book.changeCoverImageUrl(url);
        persistPort.saveBook(book);
        return toDto(book);
    }

    @Override
    public BookDto.Basic queryBook(BookId id) {
        BookJpaEntity entity = bookJpaRepository.findById(id.toLong()).get();
        return new BookDto.Basic(
            new TSID(entity.getId()),
            entity.getTitle(),
            entity.getCoverImageUrl()
        );
    }

    @Override
    public Tuple2<AuthorDto, List<BookDto.Basic>> queryBooksByAuthorId(UID uid) {
        // books
        List<BookJpaEntity> entities = bookJpaRepository.findWithAuthorByAuthorId(uid.toLong());
        List<BookDto.Basic> books =  entities.stream()
            .map(entity -> new BookDto.Basic(
                new TSID(entity.getId()),
                entity.getTitle(),
                entity.getCoverImageUrl()
            ))
            .toList();

        // author
        ProfileJpaEntity authorEntity = entities.get(0).getAuthor();
        AuthorDto author = new AuthorDto(
            new TSID(authorEntity.getId()),
            authorEntity.getPenname(),
            authorEntity.getProfileImageUrl()
        );

        return new Tuple2<>(author, books);
    }

    @Override
    public FolderDto.Basic queryFolder(NodeId folderId) {
        NodeJpaEntity entity = nodeJpaRepository.findById(folderId.toLong()).get();
        return new FolderDto.Basic(
            new TSID(entity.getId()),
            entity.getTitle()
        );
    }

    @Override
    public SectionDto.MetaData querySectionMetadata(NodeId sectionId) {
        SectionJpaEntity entity = (SectionJpaEntity) nodeJpaRepository.findById(sectionId.toLong()).get();
        return new SectionDto.MetaData(
            new TSID(entity.getId()),
            entity.getTitle()
        );
    }

    @Override
    public SectionDto.WithContent querySectionWithContent(NodeId sectionId) {
        SectionJpaEntity entity = (SectionJpaEntity) nodeJpaRepository.findById(sectionId.toLong()).get();
        return new SectionDto.WithContent(
            new TSID(entity.getId()),
            entity.getTitle(),
            entity.getContent()
        );
    }

    private BookDto.Basic toDto(Book book) {
        return new BookDto.Basic(
            book.getId().getValue(),
            book.getTitle().getValue(),
            book.getCoverImageUrl().getValue()
        );
    }
}
