package ru.isin.security.service.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import ru.isin.security.core.exception.JwtAuthException;
import ru.isin.security.domain.dto.responce.ErrorResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Create JWT filter.
 *
 * @author Bleedy (elderrat23@gmail.com) (28.11.2020)
 */
@Component
public class JwtTokenFilter extends GenericFilterBean {

	private final JwtTokenProvider jwtTokenProvider;
	private final ObjectMapper mapper;

	public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.mapper = new ObjectMapper();
	}

	/**
	 * Достаем токен, проверяем и передаем права и аутентификатор.
	 * Если при проверки не возникает ошибок, передаем данные следующим фильтрам.
	 *
	 * @param servletRequest  пришедший запрос
	 * @param servletResponse переменная ответа, через которую отправляем ошибку проверки токена
	 * @param filterChain     цепочка фильтров, через которую проходят запросы
	 * @throws IOException      может возникнуть при вызове метода sendError
	 * @throws ServletException может возникнуть при вызове метода doFilter
	 */
	@Override
	public void doFilter(ServletRequest servletRequest,
						 ServletResponse servletResponse,
						 FilterChain filterChain
	) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		HttpServletRequest request = (HttpServletRequest) servletRequest;

		String token = jwtTokenProvider.resolveToken(request); // Достаем токен из запроса
		try {
			if (token != null && jwtTokenProvider.validateToken(token)) { // Проверяем и валидируем токен
				Authentication authentication = jwtTokenProvider.getAuthentication(token); //  Передаем права
				if (authentication != null) {
					SecurityContextHolder.getContext().setAuthentication(authentication); // Передаем аутентификатор
				}
			}
		} catch (JwtAuthException e) {
			if (!response.isCommitted()) {
				SecurityContextHolder.clearContext();
				//очистка буфера ответа
				response.resetBuffer();
				//установка статуса и заголовков и сообщени отправляемой пользователю ошибки
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setHeader("Content-Type", "application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().print(mapper.writeValueAsString(new ErrorResponse(
						"Jwt token is expired or invalid",
						HttpStatus.UNAUTHORIZED,
						request.getServletPath()
				)));
				//отправка ошибки
				response.flushBuffer();
			}
		}
		// Если нет ошибок отправляем данные дальше по цепочке
		filterChain.doFilter(servletRequest, servletResponse);
	}
}
