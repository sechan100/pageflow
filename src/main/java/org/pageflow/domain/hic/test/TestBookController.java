package org.pageflow.domain.hic.test;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.repository.AccountRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/testBook")
@Builder
public class TestBookController {

    private final TestBookService testBookService;
    private final AccountRepository accountService;

    @GetMapping("/list")
    public String list(Model model){
        List<TestBook> testBookList = this.testBookService.getTestList();
        model.addAttribute("testBookList", testBookList);
        return "/testBook/testbook_list";
    }
    // 책 목록

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id) {
        TestBook testBook = this.testBookService.getTestBook(id);
        model.addAttribute("testBook", testBook);
        return "/testBook/testbook_detail";
    }
    // 책 상세페이지

    @GetMapping("/delete/{id}")
    public String testBookDelete(Principal principal, @PathVariable("id") Integer id) {
        TestBook testBook = this.testBookService.getTestBook(id);

        this.testBookService.delete(testBook);
        return "redirect:/";
    }
    // 책 삭제

    @GetMapping("/vote/{id}")
    public String testBookVote(Principal principal, @PathVariable("id") Integer id) {
        TestBook testBook = this.testBookService.getTestBook(id);
        Account user = this.accountService.findByUsernameWithProfile(principal.getName());
        this.testBookService.vote(testBook, user);
        return "redirect:/testBook/detail/{id}";
    }
    // 책 추천

    @DeleteMapping("/vote/{id}")
    public String testBookDeleteVote(Principal principal, @PathVariable("id") Integer id) {
        TestBook testBook = this.testBookService.getTestBook(id);
        Account user = this.accountService.findByUsernameWithProfile(principal.getName());
        this.testBookService.deletelVote(testBook, user);
        return "redirect:/book/list";
    }
}
