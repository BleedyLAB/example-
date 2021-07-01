package ru.isin.security.service.OAuth2;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import ru.isin.security.core.utils.Cookies;
import ru.isin.security.service.properties.JwtProperties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

import static com.nimbusds.oauth2.sdk.util.StringUtils.isNotBlank;

/**
 * Репозиторий OAuth2 Authorization Request.
 *
 * @author Bleedy (elderrat23@gmail.com) (22.02.2020)
 */
@Component
public class HttpCookieOAuth2AuthorizationRequestRepository
		implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {


	private final JwtProperties jwtProperties;

	private static final int COOKIE_EXPIRE_SECONDS = 180;

	private static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "OAUTH2-AUTH-REQUEST";

	public HttpCookieOAuth2AuthorizationRequestRepository(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
	}


	/**
	 * Достает куки из запроса затем приводит его к классу OAuth2AuthorizationRequest.
	 *
	 * @param request запрос на аутентификацию
	 * @return куки OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME из переменной request, либо null.
	 */
	@Override
	public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
		return Cookies.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
				.map(cookie -> deserialize(cookie, OAuth2AuthorizationRequest.class))
				.orElse(null);
	}

	/**
	 * Если переданный в метод запрос на авторизацию пустой, то вызывает метод removeAuthorizationRequestCookies
	 * и заканчивает работу. Если нет, то к переменной response, добавляет к response куки
	 * OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, хранящий authorizationRequest
	 * Если в request есть параметр redirectURL, то добавляем соответствующий куки в response.
	 *
	 * @param authorizationRequest запрос на аутентификацию
	 * @param request              запрос Oauth
	 * @param response             ответ на аутентификацию
	 */
	@Override
	public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
										 HttpServletRequest request, HttpServletResponse response) {
		if (authorizationRequest == null) {
			removeAuthorizationRequestCookies(request, response);
			return;
		}

		Cookies.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
				serialize(authorizationRequest), COOKIE_EXPIRE_SECONDS);
		String redirectUriAfterLogin = request.getParameter(jwtProperties.getRedirectURL());
		if (isNotBlank(redirectUriAfterLogin)) {
			Cookies.addCookie(response, jwtProperties.getRedirectURL(), redirectUriAfterLogin, COOKIE_EXPIRE_SECONDS);
		}
	}

	/**
	 * Отправляет пришедший запрос методу loadAuthorizationRequest.
	 *
	 * @param request запрос
	 * @return результат вызова метода loadAuthorizationRequest
	 */
	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
		return loadAuthorizationRequest(request);
	}

	/**
	 * удаляет куки OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME и куки
	 * с ссылкой редиректа из переданных запроса и ответа.
	 *
	 * @param request  запрос на удаление куки
	 * @param response ответ на запрос
	 */
	public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
		Cookies.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
		Cookies.deleteCookie(request, response, jwtProperties.getRedirectURL());
	}

	private static String serialize(Object object) {
		return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
	}

	/**
	 * C помощью методов cast и SerializationUtils.deserialize приводит переданный объект
	 * cookie к переданному объекту класса Class.
	 *
	 * @param cookie куки для десиарилизации
	 * @param clazz  класс к которому производится каст
	 * @return куки приведенные к переданному классу
	 */
	@SuppressWarnings("SameParameterValue")
	private static OAuth2AuthorizationRequest deserialize(Cookie cookie, Class<OAuth2AuthorizationRequest> clazz) {
		return clazz.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
	}
}
