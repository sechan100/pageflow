package org.pageflow.boundedcontext.book.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.book.command.AbstractAddNewChildCmd;
import org.pageflow.boundedcontext.book.command.AddNewFolderCmd;
import org.pageflow.boundedcontext.book.command.AddNewPageCmd;
import org.pageflow.boundedcontext.book.entity.*;
import org.pageflow.boundedcontext.book.domain.Outline;
import org.pageflow.boundedcontext.book.domain.OutlineFolder;
import org.pageflow.boundedcontext.book.domain.OutlineParentNode;
import org.pageflow.boundedcontext.book.repository.BookRepo;
import org.pageflow.boundedcontext.book.repository.FolderRepo;
import org.pageflow.boundedcontext.book.repository.ChildNodeRepo;
import org.pageflow.boundedcontext.book.repository.PageRepo;
import org.pageflow.boundedcontext.user.entity.ProfileEntity;
import org.pageflow.shared.annotation.CommandService;
import org.springframework.context.event.EventListener;

import java.util.List;

/**
 * @author : sechan
 */
@CommandService
@RequiredArgsConstructor
public class OutlineService {
    private final BookRepo bookRepo;
    private final FolderRepo folderRepo;
    private final PageRepo pageRepo;
    private final ChildNodeRepo childNodeRepo;

    /**
     * <p> 영속화된 book을 반환한다.</p>
     * @param author 작성자
     */
    public BookEntity createNewBook(ProfileEntity author){
        BookEntity book = bookRepo.save(new BookEntity(author));
        return book;
    }

    @EventListener
    public void addNewChild(AbstractAddNewChildCmd cmd){
        OutlineParentNode parentNode = cmd.getParentNode();

        // 부모 조회
        ParentNodeEntity parentEntityProxy;
        if(parentNode instanceof Outline){
            parentEntityProxy = bookRepo.getReferenceById(parentNode.getId().toLong());
        } else if(parentNode instanceof OutlineFolder){
            parentEntityProxy = folderRepo.getReferenceById(parentNode.getId().toLong());
        } else {
            assert false; return;
        }

        // 자식 생성
        ChildNodeEntity newChild;
        if(cmd instanceof AddNewPageCmd){
            newChild = new PageEntity(parentEntityProxy);
        } else if(cmd instanceof AddNewFolderCmd){
            newChild = new FolderEntity(parentEntityProxy);
        } else {
            assert false; return;
        }

        if(cmd.getTitleOrNull() != null){
            newChild.setTitle(cmd.getTitleOrNull());
        }

        childNodeRepo.save(newChild);
    }

    public List<ChildNodeEntity> getOutline(BookEntity book){
        List<ChildNodeEntity> nodes = childNodeRepo.findByBookOrderByParentNode_Id(book);
        return nodes;
    }

}
