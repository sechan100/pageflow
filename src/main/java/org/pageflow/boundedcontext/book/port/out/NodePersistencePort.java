package org.pageflow.boundedcontext.book.port.out;

import org.pageflow.boundedcontext.book.domain.Folder;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.Page;
import org.pageflow.boundedcontext.book.port.in.CreateFolderCmd;
import org.pageflow.boundedcontext.book.port.in.CreatePageCmd;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface NodePersistencePort {
    Folder createFolder(CreateFolderCmd cmd);
    Page createPage(CreatePageCmd cmd);
    Optional<Folder> loadFolder(NodeId id);
    Optional<Page> loadPage(NodeId id);
    Folder saveFolder(Folder folder);
    Page savePage(Page page);
    void deleteNode(NodeId id);
}
