package ru.isin.security.endpoint.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.isin.core.utils.log.tree.annotation.Profiled;
import ru.isin.security.domain.dto.responce.BaseResponse;
import ru.isin.security.domain.dto.responce.AuthenticationResponse;
import ru.isin.security.domain.repository.SecurityRepository;
import ru.isin.security.endpoint.utils.FingerprintUtil;
import ru.isin.security.entities.user.SecurityUser;
import ru.isin.security.service.jwt.JwtTokenProvider;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@Component
@RestController
@AllArgsConstructor
public class OAuthController {
	private final SecurityRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;

	/**
	 * Перенаправление при успешной авторизации для выдачи токенов.
	 *
	 * @param token        исользует реквест для получения данных
	 * @param servletRequest даггые запроса
	 * @return возвращает ответ в виде HttpStatus.
	 */
	@GetMapping("/loginSuccess")
	public ResponseEntity<? extends BaseResponse> authorize(@RequestParam String token,
															HttpServletRequest servletRequest) {
		String email = jwtTokenProvider.getTokenSubject(token);
		SecurityUser user = userRepository.findByEmail(email)
				.orElseThrow(() -> new AuthenticationServiceException("User doesn't exist"));

		Map<String, String> fingerprint = new HashMap<>();
		fingerprint.put("role", user.getRole().getRole());
		fingerprint.put("ipAdr", FingerprintUtil.getIpAdr(servletRequest));
		String refreshToken = jwtTokenProvider.createRefreshToken(fingerprint, jwtTokenProvider.getTokenSubject(token));
		user.setRefreshToken(refreshToken);
		userRepository.save(user);
		return ResponseEntity.ok(new AuthenticationResponse(token, refreshToken));
	}
}