package ru.isin.security.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.isin.core.utils.log.tree.annotation.Profiled;
import ru.isin.security.entities.user.SecurityUser;

import java.util.Optional;



@Repository
public interface SecurityRepository extends JpaRepository<SecurityUser, Long> {

	Optional<SecurityUser> findByEmail(String email);

	Optional<SecurityUser> findByName(String name);
}
