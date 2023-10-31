package org.pageflow.domain.book.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.form.BookForm;
import org.pageflow.domain.book.form.CommentForm;
import org.pageflow.domain.book.form.ReplyForm;
import org.pageflow.domain.book.service.BookService;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class BookController {

    private final BookService bookService;
    private final AccountService accountService;

    @GetMapping("/book/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Book> paging = this.bookService.getList(page, kw);
        model.addAttribute("paging",paging);
        model.addAttribute("kw", kw);
        return "/book/book_list";
    }

    @GetMapping("/book/create")
    public String createBook(BookForm bookForm) {
      return "/book/book_form";
    }

    @PostMapping("/book/create")
    public String createBook(@Valid BookForm bookForm, BindingResult bindingResult, @RequestParam("file") MultipartFile file, Principal principal) throws IOException {
        if(bindingResult.hasErrors() || file.isEmpty()) {
            return "/book/book_form";
        }
        Account author = this.accountService.getUser(principal.getName());
        this.bookService.create(bookForm.getTitle(), bookForm.getFile(), author);
        return "redirect:/book/list";
    }
    @GetMapping("/book/vote/{id}")
    public String bookVote(Principal principal, @PathVariable("id") Long id) {
        Book book = this.bookService.getBook(id).orElseThrow();
        Account user = this.accountService.getUser(principal.getName());
        this.bookService.vote(book, user);
        return "redirect:/book/list";
    }

    @RequestMapping("/book/detail/{id}")
    public String detail(Model model,
                         @PathVariable("id") Long id,
                         CommentForm commentForm, ReplyForm ReplyForm,
                         @RequestParam(value="page", defaultValue="0") int page,
                         HttpServletRequest request,
                         HttpServletResponse response) {

        Book book = this.bookService.getBook(id).orElseThrow();

        /* 조회수 로직 */
        Cookie oldCookie = null; //oldCookie 초기화
        Cookie[] cookies = request.getCookies();
        //request객체에서 쿠키들을 가져와 cookie 타입을 요소로 가지는 리스트에 담는다.

        if (cookies != null) { //request받은 쿠키가 있다면
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("postView")) {
                    // "postView"이름을 가진 쿠키를 찾아서

                    oldCookie = cookie; //oldcookie 에 대입
                }
            }
        }

        if (oldCookie != null) {
            //oldcookie가 null이 아니면, "postViw"쿠기가 있을 떄

            if (!oldCookie.getValue().contains("["+ id.toString() +"]")) {
                //oldCookie의 value중 게시물의 id값이 없을때(있으면 조회한 게시물로 조회수가 올라가지 않음

                this.bookService.updateView(id);

                oldCookie.setValue(oldCookie.getValue() + "_[" + id + "]");

                oldCookie.setPath("/"); // oldCookie에 경로
                oldCookie.setMaxAge(60 * 60 * 24);// 쿠키 시간
                response.addCookie(oldCookie);
            }
        } else { //oldCookie가 null일 때
            this.bookService.updateView(id); //조회수 증가 메서드 호출

            Cookie newCookie = new Cookie("postView", "[" + id + "]");
            //"postView"이름으로 쿠기 생성. 거기에 게시물 id값을 괄호로 감싸 추가.

            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24);// 쿠키 시간
            response.addCookie(newCookie);
        }

        return "/book/book_detail";
    }
        Account author = this.accountService.findByUsernameWithProfile(principal.getName());
        this.bookService.create(bookForm.getTitle(), bookForm.getFile(), author);
        return "redirect:/book/list";
    }

    @GetMapping("/book/vote/{id}")
    public String bookVote(Principal principal, @PathVariable("id") Long id) {
        Book book = this.bookService.getBook(id).orElseThrow();
        Account user = this.accountService.findByUsernameWithProfile(principal.getName());
        this.bookService.vote(book, user);
        return "redirect:/book/list";
    }
}
