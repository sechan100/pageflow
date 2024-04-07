package org.pageflow.boundedcontext.user.dto.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author : sechan
 */
public class EncodedPassword {

    @JsonIgnore
    private final String encodedPassword;

    public EncodedPassword(String encodedPassword){
        this.encodedPassword = encodedPassword;
    }

    public String getEncodedPassword(){
        return encodedPassword;
    }
}
