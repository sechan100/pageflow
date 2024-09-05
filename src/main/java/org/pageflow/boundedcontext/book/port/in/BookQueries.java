package org.pageflow.boundedcontext.book.port.in;

import io.vavr.Tuple2;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.dto.AuthorDto;
import org.pageflow.boundedcontext.book.dto.BookDto;
import org.pageflow.boundedcontext.book.dto.FolderDto;
import org.pageflow.boundedcontext.book.dto.SectionDto;
import org.pageflow.boundedcontext.common.value.UID;

import java.util.List;

/**
 * @author : sechan
 */
public interface BookQueries {
    BookDto.Basic queryBook(BookId id);
    Tuple2<AuthorDto, List<BookDto.Basic>> queryBooksByAuthorId(UID uid);


    FolderDto.Basic queryFolder(NodeId folderId);
    SectionDto.MetaData querySectionMetadata(NodeId sectionId);
    SectionDto.WithContent querySectionWithContent(NodeId sectionId);

}
