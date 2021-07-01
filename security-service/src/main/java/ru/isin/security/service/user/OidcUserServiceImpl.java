package ru.isin.security.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import ru.isin.security.domain.repository.RoleRepository;
import ru.isin.security.entities.user.SecurityUser;
import ru.isin.security.service.notification.SecurityNotification;
import ru.isin.security.core.utils.RandomStringGenerator;

import java.util.Map;

/**
 * Сервис для Oidc user.
 *
 * @author Bleedy (elderrat23@gmail.com) (22.02.2020)
 */
@Component
@RequiredArgsConstructor
public class OidcUserServiceImpl extends OidcUserService {

	private final RoleRepository roleRepository;
	private final SecurityUserService securityUserService;
	private final SecurityNotification securityNotification;

	/**
	 * Создает нового пользователя или загружает из бд.
	 *
	 * @param userRequest запрос к сервису
	 * @return авторизованный пользователь
	 * @throws OAuth2AuthenticationException ошибка OAuth2 аутентификации
	 */
	@Override
	public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
		OidcUser oidcUser = super.loadUser(userRequest);
		Map<String, Object> attributes = oidcUser.getAttributes();
		SecurityUser userToFind = securityUserService.findUserByName(String.valueOf(attributes.get("name")));
		if (userToFind.getName() == null) {
			SecurityUser user = new SecurityUser();
			user.setName(String.valueOf(attributes.get("name")));
			user.setEmail(String.valueOf(attributes.get("email")));
			user.setRole(roleRepository.findByRole("USER").orElseThrow(
					() -> new AuthenticationServiceException("Role doesn't exist")));
			user.setArchive(false);
			user.setPassword(RandomStringGenerator.getRandomString(10));
			securityUserService.saveOidcOrOauth2User(user);
			securityNotification.sendNewPasswordToOAuthUser(user.getEmail(), user.getName(), user.getPassword());
		}
		return oidcUser;
	}
}

