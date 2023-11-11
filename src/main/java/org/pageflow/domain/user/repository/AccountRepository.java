package org.pageflow.domain.user.repository;

import org.pageflow.domain.user.entity.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmailAndProvider(String email, String provider);

    Account findByUsername(String username);

    //    @Query("SELECT a FROM Account a JOIN FETCH a.profile WHERE a.username = :username")
    @EntityGraph(attributePaths = {"profile"})
    Account findFetchJoinProfileByUsername(@Param("username") String username);
}
