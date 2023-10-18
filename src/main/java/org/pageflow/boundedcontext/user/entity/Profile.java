package org.pageflow.boundedcontext.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Profile {
    
    private String email;
    
    private String nickname;
    

}
