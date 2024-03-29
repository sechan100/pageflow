package org.pageflow.boundedcontext.book.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.*;
import org.pageflow.boundedcontext.book.constants.OutlineNodeType;

/**
 * @author : sechan
 */
@Entity
@Getter
@Setter(AccessLevel.NONE)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Page extends OutlineNode {

    @Lob
    @Column(nullable = false)
    private String content;

    public Page(Folder parent){
        super(parent, OutlineNodeType.PAGE);
    }

    public void changeContent(String content) {
        this.content = content;
    }
}
