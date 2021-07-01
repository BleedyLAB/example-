package ru.isin.security.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.isin.core.utils.log.tree.annotation.Profiled;
import ru.isin.security.entities.user.PermissionEntity;



@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
}
