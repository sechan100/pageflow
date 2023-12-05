package org.pageflow.domain.support.repository;

import org.pageflow.domain.support.model.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : sechan
 */
public interface QuestionRepository extends JpaRepository<Question, Long> {
}
