//package org.pageflow.base.dev;
//
//
//import org.pageflow.domain.user.service.AccountService;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//
//import java.util.stream.IntStream;
//
//@Configuration
//@Profile("!prod")
//public class DummyDataCreator {]
//
//    @Bean
//    public ApplicationRunner init(AccountService memberService) {
//        return args -> {
//            memberService.join("admin", "1234", "admin");
//
//            IntStream.rangeClosed(1, 3).forEach(i -> {
//                memberService.join("user" + i, "1234", "nickname" + i);
//            });
//        };
//    }
//}