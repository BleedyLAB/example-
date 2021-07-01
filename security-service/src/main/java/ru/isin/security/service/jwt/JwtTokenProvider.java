package ru.isin.security.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.isin.core.utils.log.tree.annotation.Profiled;
import ru.isin.security.core.exception.JwtAuthException;
import ru.isin.security.domain.repository.SecurityRepository;
import ru.isin.security.entities.user.SecurityUser;
import ru.isin.security.service.properties.JwtProperties;
import ru.isin.security.service.user.SecurityUserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * JWT service для работы с токеном и refresh токеном.
 *
 * @author Bleedy (elderrat23@gmail.com) (28.11.2020)
 */
@Service
public class JwtTokenProvider {
	private final SecurityUserService securityUserService;
	private final SecurityRepository repository;
	private final JwtProperties jwtProperties;

	private final String secretKey;

	public JwtTokenProvider(SecurityUserService securityUserService,
							SecurityRepository repository,
							JwtProperties jwtProperties) {
		this.securityUserService = securityUserService;
		this.repository = repository;
		this.jwtProperties = jwtProperties;
		secretKey = Base64.getEncoder().encodeToString(jwtProperties.getSecret().getBytes());
	}

	/**
	 * Создает токен.
	 *
	 * @param userName имя пользователя
	 * @param role     желаемая роль
	 * @return возвращаем готовый jwt токен
	 */
	public String createTokens(String userName, String role) {
		Claims claims = Jwts.claims().setSubject(userName); // Мапа данных для генерации токена
		claims.put("role", role);
		Date now = new Date();
		Date validity = new Date(now.getTime() + jwtProperties.getExpirationToken());
		return Jwts.builder().setClaims(claims) // Передаем в билдер класс созданый выше
				.setIssuedAt(now) // Дата создания токена
				.setExpiration(validity) // Срок годности токена
				.signWith(SignatureAlgorithm.HS256, secretKey) // Настройка шифрования
				.compact();
	}

	/**
	 * Создает рефреш токен.
	 *
	 * @param userName имя пользователя
	 * @param info     информация которую поместим в токен
	 * @return возвращаем готовый jwt refresh token
	 */
	public String createRefreshToken(Map<String, String> info, String userName) {
		Claims claims = Jwts.claims().setSubject(userName);
		claims.putAll(info);
		return Jwts.builder().setClaims(claims)
				.setSubject(userName)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationRefreshToken()))
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();

	}

	/**
	 * Проверяет токен на валидность.
	 *
	 * @param token токен для проверки
	 * @return возвращаем валиден токен или нет
	 */
	public boolean validateToken(String token) {
		try {                                                                   // Парсим token с помощью secretKey
			Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			return !claimsJws.getBody().getExpiration().before(new Date()); // Проверяем не истек ли срок хранения
		} catch (JwtException | IllegalArgumentException e) {
			throw new JwtAuthException("Jwt token is expired or invalid", HttpStatus.UNAUTHORIZED);
		}
	}

	/**
	 * Авторизация пользователя.
	 *
	 * @param token передаем токен
	 * @return возвращаем токен аутентификации
	 */
	public Authentication getAuthentication(String token) {              // Достаем юзера из БД по userName
		UserDetails userDetails = this.securityUserService.loadUserByUsername(getTokenSubject(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	/**
	 * Парсим токен и достаем из него userName.
	 *
	 * @param token передаем токен
	 * @return возвращаем юзернейм из токена
	 */
	public String getTokenSubject(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}

	/**
	 * Парсим токен и достаем из него данные.
	 *
	 * @param token    передаем токен
	 * @param dataName строка ключа для поиска данных
	 * @return возвращаем юзернейм из токена
	 */
	public String getDataFromTokenBody(String token, String dataName) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get(dataName, String.class);
	}

	/**
	 * Передаем пользователю рефреш токен, для дальнейшей проверки.
	 *
	 * @param refreshToken передаем токен
	 * @param user         пользовтель для передачи токена
	 */
	public void putRefreshToken(String refreshToken, SecurityUser user) {
		user.setRefreshToken(refreshToken);
		repository.save(user);
	}

	/**
	 * Достает токен из хедера запроса.
	 *
	 * @param request реквест для получения хедера
	 * @return возвращаем хедер
	 */
	public String resolveToken(HttpServletRequest request) {
		return request.getHeader(jwtProperties.getHeader());
	}
}
