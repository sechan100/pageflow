package org.pageflow.domain.interaction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.service.BookService;
import org.pageflow.domain.interaction.entity.Comment;
import org.pageflow.domain.interaction.model.InteractionPair;
import org.pageflow.domain.interaction.model.Interactions;
import org.pageflow.domain.interaction.repository.CommentRepository;
import org.pageflow.domain.user.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : sechan
 */
@SpringBootTest
@Transactional
class BookInteractionTest {
    
    @Autowired
    private InteractionService interactionService;
    @Autowired
    private BookService bookService;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    private PreferenceService preferenceService;
    @Autowired
    private CommentRepository commentRepository;
    
    @Test
    @DisplayName("책의 모든 interaction들을 가져온다.")
    void getAllInteractions() throws JsonProcessingException {
        Book book = bookService.repoFindBookById(2L);
        Hibernate.initialize(book.getAuthor());

        
        InteractionPair<Book> pair = new InteractionPair<>(book.getAuthor(), book);
        
        Interactions interactions = interactionService.getAllInteractionsOfTarget(pair);
        assert interactions.getComments().get(0).getPreferenceStatistics().getLikes() == 0;
        assert interactions.getComments().get(0).getPreferenceStatistics().getDislikes() == 1;
        assert interactions.getPreferenceStatistics().getLikes() == 1;
        assert interactions.getPreferenceStatistics().getDislikes() == 0;
    }
    
    @Test
    @DisplayName("책에 좋아요를 누른다")
    @Commit
    void createBookPreference() {
        Book book = bookService.repoFindBookById(2L);
        InteractionPair<Book> pair = new InteractionPair<>(book.getAuthor(), book);
        preferenceService.createPreference(pair, true);
    }
    
    
    @Test
    @DisplayName("새로운 댓글을 작성한다.")
    void createComment() {
        Book book = bookService.repoFindBookById(2L);
        InteractionPair<Book> pair = new InteractionPair<>(book.getAuthor(), book);
        commentService.createComment(pair, "댓글 내용");
    }
    
    @Test
    @DisplayName("댓글을 삭제한다.")
    @Commit
    void deleteComment(){
        Book book = bookService.repoFindBookById(2L);
        InteractionPair<Book> pair = new InteractionPair<>(book.getAuthor(), book);
        Interactions interactions = interactionService.getAllInteractionsOfTarget(pair);
        Long commentId = interactions.getComments().get(0).getComment().getId();
        
        commentService.deleteComment(commentId);
    }
    
    
    @Test
    @DisplayName("댓글에 좋아요를 누른다.")
    @Commit
    void createCommentPreference() {
        Book book = bookService.repoFindBookById(2L);
        InteractionPair<Book> pair = new InteractionPair<>(book.getAuthor(), book);
        Interactions interactions = interactionService.getAllInteractionsOfTarget(pair);
        Long commentId = interactions.getComments().get(0).getComment().getId();
        
        InteractionPair<Comment> commentPair = new InteractionPair<>(profileRepository.findById(1L).orElseThrow(), commentRepository.findById(commentId).orElseThrow());
        preferenceService.createPreference(commentPair, true);
    }
    
    @Test
    @DisplayName("댓글의 좋아요를 싫어요로 바꾼다.")
    @Commit
    void toggleCommentPreference(){
        Book book = bookService.repoFindBookById(2L);
        InteractionPair<Book> pair = new InteractionPair<>(book.getAuthor(), book);
        Interactions interactions = interactionService.getAllInteractionsOfTarget(pair);
        Long commentId = interactions.getComments().get(0).getComment().getId();
        
        InteractionPair<Comment> commentPair = new InteractionPair<>(profileRepository.findById(1L).orElseThrow(), commentRepository.findById(commentId).orElseThrow());
        preferenceService.togglePreferenceIsLiked(commentPair);
    }
    
    @Test
    @DisplayName("댓글의 좋아요를 삭제한다.")
    @Commit
    void deleteCommentPreference(){
        Book book = bookService.repoFindBookById(2L);
        InteractionPair<Book> pair = new InteractionPair<>(book.getAuthor(), book);
        Interactions interactions = interactionService.getAllInteractionsOfTarget(pair);
        Long commentId = interactions.getComments().get(0).getComment().getId();
        
        InteractionPair<Comment> commentPair = new InteractionPair<>(profileRepository.findById(1L).orElseThrow(), commentRepository.findById(commentId).orElseThrow());
        preferenceService.deletePreference(commentPair);
    }
    
    
}