package org.skr.registry.repository;

import org.skr.common.util.Checker;
import org.skr.registry.model.Permission;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, String> {

    @Query("SELECT p FROM Permission p ORDER BY p.bit3, p.bit2, p.bit1 DESC")
    List<Permission> findByMaxBit(Pageable pageable);

    default Permission findMaxBitPermission() {
        List<Permission> resultList = findByMaxBit(new PageRequest(0,1));
        if (Checker.isEmpty(resultList)) return null;
        return resultList.get(0);
    }

}
