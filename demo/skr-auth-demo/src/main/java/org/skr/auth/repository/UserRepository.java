package org.skr.auth.repository;

import org.skr.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u " +
            "WHERE u.tenent.code = ?1 " +
            "AND u.account.username = ?2")
    User findOneByTenentCodeAndAccount(String tenentCode, String username);

}
