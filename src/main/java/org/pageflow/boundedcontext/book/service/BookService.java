package org.pageflow.boundedcontext.book.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.book.command.AddNewFolderCmd;
import org.pageflow.boundedcontext.book.entity.*;
import org.pageflow.boundedcontext.book.model.Outline;
import org.pageflow.boundedcontext.book.model.OutlineParentNode;
import org.pageflow.boundedcontext.book.repository.BookRepo;
import org.pageflow.boundedcontext.book.repository.FolderRepo;
import org.pageflow.boundedcontext.book.repository.OutlineNodeRepo;
import org.pageflow.boundedcontext.book.repository.PageRepo;
import org.pageflow.boundedcontext.user.entity.Profile;
import org.pageflow.shared.annotation.TransactionalService;
import org.springframework.context.event.EventListener;

import java.util.List;

/**
 * @author : sechan
 */
@TransactionalService
@RequiredArgsConstructor
public class BookService {
    private final BookRepo bookRepo;
    private final FolderRepo folderRepo;
    private final PageRepo pageRepo;
    private final OutlineNodeRepo outlineNodeRepo;

    /**
     * <p> 영속화된 book을 반환한다.</p>
     * @param author 작성자
     */
    public BookEntity createNewBook(Profile author){
        BookEntity book = bookRepo.save(new BookEntity(author));
        return book;
    }

    @EventListener
    public void addNewFolder(AddNewFolderCmd cmd){
        OutlineParentNode parentNode = cmd.getParentNode();
        ParentNodeEntity parentEntityProxy;
        if(parentNode instanceof Outline){
            parentEntityProxy = bookRepo.getReferenceById(parentNode.getId());
        } else { // else if(cmd.getParentNode() instanceof OutlineFolder folderAsParent)
            parentEntityProxy = folderRepo.getReferenceById(parentNode.getId());
        }
        folderRepo.save(new FolderEntity(parentEntityProxy));
    }

    /**
     * parent에 새로운 page를 추가하고 영속화된 page를 반환한다.
     * @param parent 부모 노드(folder, book)
     */
    public PageEntity addNewPage(ParentNodeEntity parent){
        PageEntity nestedPageEntity = new PageEntity(parent);
        return pageRepo.save(nestedPageEntity);
    }

    public List<ChildNodeEntity> getOutline(BookEntity book){
        List<ChildNodeEntity> nodes = outlineNodeRepo.findByBookOrderByParentNode_Id(book);
        return nodes;
    }

}
