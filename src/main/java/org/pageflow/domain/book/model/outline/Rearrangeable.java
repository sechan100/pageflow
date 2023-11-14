package org.pageflow.domain.book.model.outline;

public interface Rearrangeable {
    Long getId();

    String getTitle();

    Integer getSortPriority();

    Long getOwnerId();


    void setId(Long id);

    void setTitle(String title);

    void setSortPriority(Integer sortPriority);

    void setOwnerId(Long ownerId);
}
