package ru.isin.security.service.OAuth2;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import ru.isin.security.core.utils.Cookies;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Хендлер стартует если аутентификация провалилась .
 *
 * @author Bleedy (elderrat23@gmail.com) (22.02.2020)
 */
@Component
public class AuthenticationFailureHandlerImp implements AuthenticationFailureHandler {

	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

	public AuthenticationFailureHandlerImp(
			HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository) {
		this.authorizationRequestRepository = authorizationRequestRepository;
	}

	/**
	 * Удаляет куки запроса и перенаправаляет на страницу ошибки.
	 *
	 * @param httpServletRequest  запрос с ошибкой
	 * @param httpServletResponse ответ
	 * @param e                   ошибка
	 * @throws IOException возникает при вызове функции sendRedirect
	 */
	@Override
	public void onAuthenticationFailure(HttpServletRequest httpServletRequest,
										HttpServletResponse httpServletResponse,
										AuthenticationException e
	) throws IOException {
		String targetUrl = getFailureUrl(httpServletRequest, e);
		authorizationRequestRepository.removeAuthorizationRequestCookies(httpServletRequest, httpServletResponse);
		redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, targetUrl);
	}

	/**
	 * Достает из куки с REDIRECT_URI и вытаскивает из него адрес, если куки нет, то адрес будет корневым,
	 * добавляет параметр ошибки в ссылку.
	 *
	 * @param request   запрос с ошибкой
	 * @param exception ошибка
	 * @return ссылка редиректа
	 */
	private String getFailureUrl(HttpServletRequest request, AuthenticationException exception) {
		String targetUrl = Cookies.getCookie(request, Cookies.REDIRECT_URI)
				.map(Cookie::getValue)
				.orElse(("/"));

		return UriComponentsBuilder.fromUriString(targetUrl)
				.queryParam("error", exception.getLocalizedMessage())
				.build().toUriString();
	}
}
