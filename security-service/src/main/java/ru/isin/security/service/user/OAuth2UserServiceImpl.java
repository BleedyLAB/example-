package ru.isin.security.service.user;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import ru.isin.security.domain.dto.oauth.OAuth2ToUser;
import ru.isin.security.domain.repository.RoleRepository;
import ru.isin.security.entities.user.SecurityUser;
import ru.isin.security.core.utils.RandomStringGenerator;

import java.util.Map;
import java.util.Objects;

/**
 * Класс представляющий OAuth2User сервис.
 *
 * @author Bleedy (elderrat23@gmail.com) (22.02.2020)
 */
@Component
@AllArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

	private final SecurityUserService securityUserService;
	private final RoleRepository roleRepository;

	/**
	 * С помощью родительского метода loadUser загружает OAuth2User.
	 * Затем вызывается метод processOAuth2User для загрузки либо добавления пользователя в бд.
	 *
	 * @param userRequest запрос к OAuth2UserService
	 * @return загруженный user
	 */
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		try {
			return processOAuth2User(oAuth2User, userRequest);
		} catch (Exception ex) {
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
		}
	}

	/**
	 * Авторизация пользователя, в случае если он не зарегистрирован в БД, добавляем его.
	 *
	 * @param oAuth2User        пользователь
	 * @param oAuth2UserRequest запрос к OAuth2UserService
	 * @return авторизованный OAuth2User
	 */
	private OAuth2User processOAuth2User(OAuth2User oAuth2User, OAuth2UserRequest oAuth2UserRequest) {
		String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
		OAuth2UserInfo userInfo = new OAuth2UserInfo(oAuth2User.getAttributes());

		SecurityUser user = findOrCreate(userInfo, registrationId);
		return new OAuth2ToUser(user, oAuth2User.getAttributes());
	}

	/**
	 * Проверяем есть ли пользователь в базе, если нет то создаем.
	 *
	 * @param userInfo       информация о пользователе
	 * @param registrationId id метода регистрации
	 * @return созданный или найденный в бд пользователь
	 */
	private SecurityUser findOrCreate(OAuth2UserInfo userInfo, String registrationId) {
		SecurityUser userToFind = securityUserService.findUserByName(String.valueOf(userInfo.attrs.get("login")));
		if (userToFind.getName() == null) {
			if ("github".equals(registrationId)) {
				userToFind = gitHubOAuth2(userInfo);
			}
		}
		return userToFind;
	}

	/**
	 * Создает нового пользователя по переданным параметрам.
	 *
	 * @param userInfo информация о пользователе
	 * @return новый пользователь авторизованный через github
	 */
	private SecurityUser gitHubOAuth2(OAuth2UserInfo userInfo) {
		SecurityUser user = new SecurityUser();
		user.setName(String.valueOf(userInfo.attrs.get("login")));
		user.setRole(roleRepository.findByRole("USER").orElseThrow(
				() -> new AuthenticationServiceException("Role doesn't exist")));
		user.setArchive(false);
		user.setPassword(RandomStringGenerator.getRandomString(10));
		user.setEmail(RandomStringGenerator.getRandomString(8) + "@temp.temp");
		return securityUserService.createUser(user);
	}

	/**
	 * Класс объекты которого хранят информацию о пользователе.
	 */
	private static class OAuth2UserInfo {
		private final Map<String, Object> attrs;

		/**
		 * Объект OAuth2UserInfo создаётся по переданным атрибутам.
		 *
		 * @param attrs атрибуты пользователя
		 */
		private OAuth2UserInfo(Map<String, Object> attrs) {
			this.attrs = Objects.requireNonNull(attrs);
		}

		public String getName() {
			return getAttribute("display_name");
		}

		public String getLogin() {
			return getAttribute("username");
		}

		@SuppressWarnings("unchecked")
		private <A> A getAttribute(String attrName) {
			return (A) attrs.get(attrName);
		}
	}
}