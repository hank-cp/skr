package org.skr.auth.repository;

import org.skr.auth.model.Tenent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenentRepository extends JpaRepository<Tenent, Long> {
}
