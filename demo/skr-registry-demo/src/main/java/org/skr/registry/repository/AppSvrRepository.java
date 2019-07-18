package org.skr.registry.repository;

import org.skr.registry.model.AppSvr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppSvrRepository extends JpaRepository<AppSvr, String> {

}
