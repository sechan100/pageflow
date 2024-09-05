package org.pageflow.boundedcontext.book.port.out;

import org.pageflow.boundedcontext.book.domain.Folder;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.Section;
import org.pageflow.boundedcontext.book.port.in.FolderCreateCmd;
import org.pageflow.boundedcontext.book.port.in.SectionCreateCmd;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface NodePersistencePort {
    Folder createFolder(FolderCreateCmd cmd);
    Section createSection(SectionCreateCmd cmd);
    Optional<Folder> loadFolder(NodeId id);
    Optional<Section> loadSection(NodeId id);
    Folder saveFolder(Folder folder);
    Section saveSection(Section section);
    void deleteNode(NodeId id);
}
