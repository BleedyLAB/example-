package ru.isin.security.service.user;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.isin.core.errors.exception.BusinessException;
import ru.isin.security.core.exception.SecurityExceptionReason;
import ru.isin.security.domain.repository.RoleRepository;
import ru.isin.security.domain.repository.SecurityRepository;
import ru.isin.security.entities.user.RoleEntity;
import ru.isin.security.entities.user.SecurityUser;

import java.util.List;
import java.util.Optional;

/**
 * Service for work with UserDetail.
 *
 * @author Bleedy (elderrat23@gmail.com) (28.11.2020)
 */
@Service
@AllArgsConstructor
public class SecurityUserDetailServiceImpl implements SecurityUserService {

	private final SecurityRepository securityRepository;
	private final RoleRepository roleRepository;
	private final TriggerSecurityService triggerSecurityService;


	/**
	 * Если в бд есть пользователь с email name то возвращает его.
	 * Если нет то пытается найти пользователя с именем name.
	 *
	 * @param name имя пользователя или email
	 * @return user для spring security
	 * @throws UsernameNotFoundException возникает, если пользователя не существует в бд
	 */
	@Override
	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
		if (securityRepository.findByEmail(name).isPresent()) {

			return securityRepository.findByEmail(name).orElseThrow(
					() -> new UsernameNotFoundException("User doesn't exists"));
		} else {
			return securityRepository.findByName(name).orElseThrow(
					() -> new UsernameNotFoundException("User doesn't exists"));
		}
	}

	/**
	 * Поиск пользователя в бд по переданному id, если такого нет, то возвращает нового пользователя.
	 *
	 * @param userId id искомого пользователя
	 * @return найденный пользователь
	 */
	@Override
	public SecurityUser findUserById(Long userId) {
		Optional<SecurityUser> userFromDb = securityRepository.findById(userId);
		return userFromDb.orElse(new SecurityUser());
	}

	/**
	 * Поиск пользователя в бд по переданному name, если такого нет, то возвращает нового пользователя.
	 *
	 * @param name имя пользователя
	 * @return пользователь найденный по имени
	 */
	@Override
	public SecurityUser findUserByName(String name) {
		Optional<SecurityUser> userFromDb = securityRepository.findByName(name);
		return userFromDb.orElse(new SecurityUser());
	}

	/**
	 * @return все пользователи в бд.
	 */
	@Override
	public List<SecurityUser> findAllUsers() {
		return securityRepository.findAll();
	}

	/**
	 * Добавление пользователя в бд.
	 *
	 * @param user пользователь для добавления
	 * @return если не возникло ошибок возвращается новый пользователь.
	 */
	@Override
	public SecurityUser saveUser(SecurityUser user) {
		SecurityUser newUser = new SecurityUser();
		newUser.setName(user.getName());
		newUser.setEmail(user.getEmail());
		newUser.setRole(roleRepository.findByRole("USER").orElseThrow(
				() -> new AuthenticationServiceException("Role doesn't exist")));
		newUser.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
		newUser.setArchive(false);
		triggerSecurityService.executeBefore(newUser);
		triggerSecurityService.executeAfter(securityRepository.save(newUser));
		return newUser;
	}

	/**
	 * Добавление пользователя в бд.
	 * Отличен от предыдущего тем, что почта активна.
	 *
	 * @param user пользователь для добавления
	 * @return если не возникло ошибок возвращается новый пользователь
	 */
	@Override
	public SecurityUser saveOidcOrOauth2User(SecurityUser user) {
		SecurityUser newUser = new SecurityUser();
		newUser.setName(user.getName());
		newUser.setEmail(user.getEmail());
		newUser.setRole(roleRepository.findByRole("USER").orElseThrow(
				() -> new AuthenticationServiceException("Role doesn't exist")));
		newUser.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
		newUser.setEmailConfirmed(true);
		triggerSecurityService.executeBefore(newUser);
		triggerSecurityService.executeAfter(securityRepository.save(newUser));
		return newUser;
	}

	/**
	 * Создает и возвращает пользователя.
	 *
	 * @param user новый пользователь
	 * @return новый пользователь
	 */
	@Override
	public SecurityUser createUser(SecurityUser user) {
		saveUser(user);
		return user;
	}

	/**
	 * Устанавливает роль ADMIN для пользователя по переданной почте если пользователь существует.
	 *
	 * @param email почта пользователя
	 * @return возвращает потльзователя
	 */
	@Override
	public SecurityUser makeAdminRole(String email) {
		SecurityUser user = securityRepository.findByEmail(email).orElseThrow(
				() -> new UsernameNotFoundException("User doesn't exists"));
		user.setRole(roleRepository.findByRole("ADMIN").orElseThrow(
				() -> new AuthenticationServiceException("Role doesn't exist")));
		securityRepository.save(user);
		return user;
	}

	/**
	 * Обновление данных пользователя по почте.
	 *  @param email почта пользователя
	 * @param user  пользователь с обновленными данными
	 * @return возвращает измененного пользователя
	 */
	@Override
	public SecurityUser updateUser(String email, SecurityUser user) {
		SecurityUser oldUser = securityRepository.findByEmail(email).orElseThrow(
				() -> new UsernameNotFoundException("User doesn't exists"));
		oldUser.setEmail(user.getEmail() == null
				? oldUser.getEmail() : user.getEmail());
		oldUser.setPassword(user.getPassword() == null
				? oldUser.getPassword() : new BCryptPasswordEncoder().encode(user.getPassword()));
		securityRepository.save(oldUser);
		return oldUser;
	}

	/**
	 * Ищет в бд пользователя по username, если существует, то возвращает его роль, если нет то дефолтную роль.
	 *
	 * @param username имя пользователя
	 * @return роль искомого пользователя
	 */
	public RoleEntity getUserRoleByName(String username) {
		return securityRepository.findByName(username).orElse(new SecurityUser()).getRole();
	}

	/**
	 * Ищет в бд пользователя по email, если существует, то возвращает его роль, если нет то дефолтную роль.
	 *
	 * @param email email пользователя
	 * @return роль искомого пользователя
	 */
	public RoleEntity getUserRoleByEmail(String email) {
		return securityRepository.findByEmail(email).orElse(new SecurityUser()).getRole();
	}

	/**
	 * Ищет в бд пользователя по email, если существует, то подтверждает его почту.
	 *
	 * @param email email пользователя
	 * @return возвращает пользователя.
	 */
	@Override
	public SecurityUser confirmMail(String email) throws BusinessException {
		SecurityUser user = securityRepository
				.findByEmail(email)
				.orElseThrow(() ->
						new BusinessException(SecurityExceptionReason.USER_NOT_FOUND_BY_EMAIL, email)
				);
		user.setEmailConfirmed(true);
		securityRepository.save(user);
		return user;
	}
}
