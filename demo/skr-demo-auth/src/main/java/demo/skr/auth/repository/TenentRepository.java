package demo.skr.auth.repository;

import demo.skr.auth.model.Tenent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenentRepository extends JpaRepository<Tenent, Long> {
}
