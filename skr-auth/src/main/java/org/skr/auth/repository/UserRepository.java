package org.skr.auth.repository;

import org.skr.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u " +
            "WHERE u.organization.code = ?1 " +
            "AND u.account.username = ?2")
    User findOneByOrgCodeAndAccount(String orgCode, String username);

}
