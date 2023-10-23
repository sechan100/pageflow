package org.pageflow.infra.util;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;



public class Ut {

    public static final Generator generator = new Generator();
    public static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

}
