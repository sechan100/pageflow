package org.pageflow.domain.hic.test;

import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.DataNotFoundException;
import org.pageflow.domain.user.entity.Account;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class TestBookService {

    private final TestBookRepository testBookRepository;

    @Transactional
    public List<TestBook> getTestList(){
        List<TestBook> testBooks = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            TestBook testBook = TestBook
                    .builder()
                    .title("Title " + i)
                    .build();

            testBooks.add(testBook);
        }

        // 데이터베이스에 저장
        testBookRepository.saveAll(testBooks);

        return testBooks;
    } // 데이터가 없을 때만 생성 > book데이터 안정화되면 리포지터리에 저장하는 코드 변경 해야함
    // 임시데이터 생성 후 저장

    public TestBook getTestBook(Integer id) {
        Optional<TestBook> testBook = this.testBookRepository.findById(id);
        if (testBook.isPresent()) {
            return testBook.get();
        } else {
            throw new DataNotFoundException("testBook not found");
        }
    }
    // 책 찾기

    public void delete(TestBook testBook) {
        this.testBookRepository.delete(testBook);
    }
    // 책 삭제

    public void vote(TestBook testBook, Account siteUser) {
        testBook.getVoter().add(siteUser);
        this.testBookRepository.save(testBook);
    }
    // 책 추천

    public void deletelVote(TestBook testBook, Account user) {
        testBook.getVoter().remove(user);
        this.testBookRepository.save(testBook);
    }

}
