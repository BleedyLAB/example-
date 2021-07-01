package ru.isin.security.service.user;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.isin.core.errors.exception.BusinessException;
import ru.isin.security.entities.user.RoleEntity;
import ru.isin.security.entities.user.SecurityUser;

import java.util.List;

/**
 * Basic service for spring security.
 *
 * @author Bleedy (elderrat23@gmail.com) (28.11.2020)
 */
public interface SecurityUserService extends UserDetailsService {

	SecurityUser findUserById(Long userId);

	SecurityUser findUserByName(String user);

	List<SecurityUser> findAllUsers();

	SecurityUser saveUser(SecurityUser user);

	SecurityUser saveOidcOrOauth2User(SecurityUser user);

	SecurityUser createUser(SecurityUser user);

	SecurityUser makeAdminRole(String email);

	SecurityUser updateUser(String email, SecurityUser user);

	RoleEntity getUserRoleByName(String email);

	RoleEntity getUserRoleByEmail(String email);

	SecurityUser confirmMail(String email) throws BusinessException;

}
