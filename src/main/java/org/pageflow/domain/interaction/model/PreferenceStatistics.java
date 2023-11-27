package org.pageflow.domain.interaction.model;

import lombok.Data;
import org.pageflow.domain.interaction.entity.Preference;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : sechan
 */
@Data
public class PreferenceStatistics {
    private Long targetId;
    private String targetType;
    private List<Preference> likes = new ArrayList<>();
    private List<Preference> dislikes = new ArrayList<>();
}
