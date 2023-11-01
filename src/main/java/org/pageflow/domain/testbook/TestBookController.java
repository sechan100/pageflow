package org.pageflow.domain.testbook;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.service.AccountService;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TestBookController {
    private final TestBookService testBookService;
    private final AccountService accountService;

    @GetMapping("/testbook/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        Slice<TestBook> paging = this.testBookService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "/testbook/testbook_list";
    }

    @GetMapping("/api/testbook/list")
    public ResponseEntity<List<TestBook>> getTestBooksApi(@RequestParam(value = "page", defaultValue = "0") int page,
                                                          @RequestParam(value = "kw", defaultValue = "") String kw) {
        Slice<TestBook> paging = this.testBookService.getList(page, kw);
        return new ResponseEntity<>(paging.getContent(), HttpStatus.OK);
    }

    @GetMapping("/testbook/create")
    public String testBookCreate(TestBookForm testBookForm) {
        return "/testBook/testBook_form";
    }
    @PostMapping("/testbook/create")
    public String testBookCreate(@Valid TestBookForm testBookForm, BindingResult bindingResult, Principal principal) {
        if(bindingResult.hasErrors()) {
            return "/testbook/testbook_form";
        }
        Account user = this.accountService.findByUsername(principal.getName());
//        this.testBookService.create(testBookForm.getTitle(), testBookForm.getContent(),user);
        return "redirect:/testbook/list";
    }
}
