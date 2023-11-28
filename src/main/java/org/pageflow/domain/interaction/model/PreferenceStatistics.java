package org.pageflow.domain.interaction.model;

import lombok.Data;

/**
 * @author : sechan
 */
@Data
public class PreferenceStatistics {
    
    private int likes;
    private int dislikes;
    
    
    public void addLike() {
        likes++;
    }
    
    public void addDislike() {
        dislikes++;
    }
}
