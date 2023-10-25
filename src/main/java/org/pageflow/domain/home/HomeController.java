package org.pageflow.domain.home;

import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final Rq rq;

    
    @GetMapping("/")
    public String homePage() {
        
        return "/user/home/home";
    }
    
    
}
