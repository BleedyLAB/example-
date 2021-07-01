package ru.isin.security.service.OAuth2;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import ru.isin.security.core.utils.Cookies;
import ru.isin.security.entities.user.RoleEntity;
import ru.isin.security.service.jwt.JwtTokenProvider;
import ru.isin.security.service.properties.JwtProperties;
import ru.isin.security.service.user.SecurityUserService;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Хендлер стартует если аутентификация прошла успешно.
 *
 * @author Bleedy (elderrat23@gmail.com) (22.02.2020)
 */
@Component
public class AuthenticationSuccessHandlerImpl extends SimpleUrlAuthenticationSuccessHandler {
	private final JwtProperties jwtProperties;
	private final JwtTokenProvider tokenProvider;
	private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;
	private final SecurityUserService securityUserService;


	/**
	 * Метод запускается при успешной аутентификации oauth2 пользователя. Не работает с oidc.
	 *
	 * @param jwtProperties                  проперти для jwt
	 * @param tokenProvider                  Сервайс класс токенов.
	 * @param authorizationRequestRepository Репозиторий запросов.
	 * @param securityUserService            Сервис юзеров.
	 */
	public AuthenticationSuccessHandlerImpl(
			JwtProperties jwtProperties, JwtTokenProvider tokenProvider,
			HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository,
			SecurityUserService securityUserService) {
		this.jwtProperties = jwtProperties;
		this.tokenProvider = tokenProvider;
		this.authorizationRequestRepository = authorizationRequestRepository;
		this.securityUserService = securityUserService;
	}

	/**
	 * Логирует информацию о пользоваетел и вызывет родительский метод onAuthenticationSuccess.
	 *
	 * @param request        пришедший запрос
	 * @param response       отправляемый ответ
	 * @param authentication контекст безопасности приложения
	 * @throws IOException      возникает при вызове метода handle из родительского метода onAuthenticationSuccess
	 * @throws ServletException возникает при вызове метода handle из родительского метода onAuthenticationSuccess
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
										HttpServletResponse response,
										Authentication authentication
	) throws IOException, ServletException {
		logger.info("User login : " + authentication.getPrincipal().toString());

		super.onAuthenticationSuccess(request, response, authentication);
	}

	/**
	 * Получает пользователя определяет сервис аутентификации и роль пользователя,
	 * генерирует ссылку с токеном по полученной информации.
	 *
	 * @param request        пришедший запрос
	 * @param response       отправляемый ответ
	 * @param authentication контекст безопасности приложения
	 * @return ссылка редиректа с токеном
	 */
	@Override
	protected String determineTargetUrl(HttpServletRequest request,
										HttpServletResponse response,
										Authentication authentication
	) {
		String redirectURL = jwtProperties.getRedirectURL();
		Optional<String> redirectUri = Cookies.getCookie(request, redirectURL).map(Cookie::getValue);
		OAuth2AuthenticationToken info = (OAuth2AuthenticationToken) authentication;
		String username;
		RoleEntity role;
		String email;
		if (info.getAuthorizedClientRegistrationId().equals("google")) {
			DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
			Map<String, Object> attributes = oidcUser.getAttributes();
			username = String.valueOf(attributes.get("name"));
			email = String.valueOf(attributes.get("email"));
			role = securityUserService.getUserRoleByEmail(email);
		} else {
			username = authentication.getName();
			role = securityUserService.getUserRoleByName(username);
			return UriComponentsBuilder.fromUriString(redirectUri.orElse(getDefaultTargetUrl())).path(redirectURL)
					.queryParam("token", tokenProvider.createTokens(username, role.getRole()))
					.build().toUriString();
		}
		return UriComponentsBuilder.fromUriString(redirectUri.orElse(getDefaultTargetUrl())).path(redirectURL)
				.queryParam("token", tokenProvider.createTokens(email, role.getRole()))
				.build().toUriString();
	}

	/**
	 * вызов метода redirectToTargetUrl.
	 *
	 * @param request        пришедший запрос
	 * @param response       отправляемый ответ
	 * @param authentication контекст безопасности приложения
	 * @throws IOException может возникнуть при вызове метода redirectToTargetUrl
	 */
	@Override
	protected void handle(HttpServletRequest request,
						  HttpServletResponse response,
						  Authentication authentication
	) throws IOException {
		redirectToTargetUrl(request, response, authentication);
	}

	/**
	 * Вызывает метод determineTargetUrl, если ответ уже был отправлен то редирект не будет выполнен.
	 * Вызывает метод очистки кук и отправляет редирект с помощью метода sendRedirect
	 *
	 * @param request        пришедший запрос
	 * @param response       отправляемый ответ
	 * @param authentication контекст безопасности приложения
	 * @throws IOException может возникнуть при вызове метода redirectToTargetUrl
	 */
	private void redirectToTargetUrl(HttpServletRequest request,
									 HttpServletResponse response,
									 Authentication authentication
	) throws IOException {
		String targetUrl = determineTargetUrl(request, response, authentication);

		if (response.isCommitted()) {
			logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}

		authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}
