package org.pageflow.boundedcontext.book.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.boundedcontext.book.constants.OutlineNodeType;

import java.util.LinkedList;
import java.util.List;

/**
 * @author : sechan
 */
@Entity
@Getter
@Setter(AccessLevel.NONE)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
@Table(name = "folder")
public class Folder extends OutlineNode {

    @OneToMany(
        mappedBy = "parentNode",
        fetch = FetchType.LAZY,
        cascade = CascadeType.REMOVE,
        orphanRemoval = true
    )
    @OrderBy("ordinalValue ASC")
    private List<OutlineNode> children = new LinkedList<>();



    public Folder(Folder parent){
        super(parent, OutlineNodeType.FOLDER);
    }

    /**
     * for create 'root folder'
     */
    private Folder(Book book){
        super(book);
    }



    public int getLastNodeOrdinalValue(){
        if(children.isEmpty()){
            return 0;
        }
        return children.get(children.size() - 1).getOrdinalValue();
    }

    // package-private access modifier -> Book 클래스의 생성자에서 최초 root folder를 생성할 때만 사용한다.
    static Folder createRootFolder(Book book){
        if(book.getRootFolder() != null){
            throw new IllegalStateException(
                "해당 book이 이미 root folder를 가지고 있습니다. root folder는 한개만 생성할 수 있습니다."
            );
        }
        return new Folder(book);
    }
}
