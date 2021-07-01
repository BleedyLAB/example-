package ru.isin.security.endpoint.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.isin.core.utils.log.tree.annotation.Profiled;
import ru.isin.security.domain.dto.request.AuthenticationRequest;
import ru.isin.security.domain.dto.responce.BaseResponse;
import ru.isin.security.domain.dto.responce.ErrorResponse;
import ru.isin.security.domain.dto.responce.AuthenticationResponse;
import ru.isin.security.domain.repository.SecurityRepository;
import ru.isin.security.endpoint.utils.FingerprintUtil;
import ru.isin.security.entities.user.SecurityUser;
import ru.isin.security.service.jwt.JwtTokenProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for authentication.
 *
 * @author Bleedy (elderrat23@gmail.com) (28.11.2020)
 */

@RestController
@RequestMapping(AuthenticationRestController.BASE_URL)
@RequiredArgsConstructor
public class AuthenticationRestController implements AbstractRest {
	public static final String BASE_URL = AbstractRest.BASE_URL + "/auth";

	private final AuthenticationManager authenticationManager;
	private final SecurityRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;

	/**
	 * @param request        исользует реквест для получения данных
	 * @param servletRequest даггые запроса
	 * @return возвращает ответ в виде HttpStatus.
	 */
	@PostMapping("/login")
	public ResponseEntity<? extends BaseResponse> authenticate(
			@RequestBody AuthenticationRequest request,
			HttpServletRequest servletRequest
	) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					request.getEmail(),
					request.getPassword())
			);

			SecurityUser user = userRepository.findByEmail(request.getEmail())
					.orElseThrow(() -> new AuthenticationServiceException("User doesn't exist"));

			if (!user.isEmailConfirmed()) {
				return ResponseEntity.ok(
						new ErrorResponse("User haven't confirmed email",
								HttpStatus.FORBIDDEN, BASE_URL + "/login")
				);
			}

			Map<String, String> fingerprint = new HashMap<>();
			fingerprint.put("role", user.getRole().getRole());
			fingerprint.put("ipAdr", FingerprintUtil.getIpAdr(servletRequest));

			String refreshToken = jwtTokenProvider.createRefreshToken(fingerprint, request.getEmail());
			String token = jwtTokenProvider.createTokens(request.getEmail(), user.getRole().getRole());
			jwtTokenProvider.putRefreshToken(refreshToken, user);

			return ResponseEntity.ok(new AuthenticationResponse(token, refreshToken));

		} catch (AuthenticationException e) {
			System.out.println(e.getMessage());
			ErrorResponse errorResponseEntity = new ErrorResponse(
					e.getMessage(),
					HttpStatus.NOT_FOUND,
					BASE_URL + "/login"
			);
			return new ResponseEntity<>(errorResponseEntity, HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping("/logout")
	public void logOut(HttpServletRequest request, HttpServletResponse response) {
		SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
		logoutHandler.logout(request, response, null);
	}

	/**
	 * Метод для обновления старого jwt токена без необходимости повторныой авторизации.
	 *
	 * @param servletRequest данные пользователя, достаем от туда фингерпринты
	 * @param refreshToken   достаем токен из хедера запроса
	 * @return возвращает ответ в виде HttpStatus.
	 */
	@PostMapping("/refreshtoken")
	public ResponseEntity<? extends BaseResponse> refreshToken(HttpServletRequest servletRequest,
															   @RequestHeader("RefreshToken") String refreshToken
	) {
		SecurityUser user = userRepository.findByEmail(jwtTokenProvider.getTokenSubject(refreshToken))
				.orElseThrow(() -> new AuthenticationServiceException("User doesn't exist"));

		Map<String, String> fingerprint = new HashMap<>();
		fingerprint.put("ipAdr", FingerprintUtil.getIpAdr(servletRequest));

		if (jwtTokenProvider.validateToken(refreshToken)
				&& jwtTokenProvider.getDataFromTokenBody(refreshToken, "ipAdr").equals(fingerprint.get("ipAdr"))
				&& user.getRefreshToken().equals(refreshToken)) {

			String role = jwtTokenProvider.getDataFromTokenBody(refreshToken, "role");
			fingerprint.put("role", role);

			String tokenSubject = jwtTokenProvider.getTokenSubject(refreshToken);
			String refToken = jwtTokenProvider.createRefreshToken(fingerprint, tokenSubject);
			jwtTokenProvider.putRefreshToken(refToken, user);

			return ResponseEntity.ok(
					new AuthenticationResponse(jwtTokenProvider.createTokens(tokenSubject, role), refToken));
		}

		return new ResponseEntity<>(new ErrorResponse("Token is invalid", HttpStatus.NOT_FOUND,
				BASE_URL + "/refreshtoken"), HttpStatus.NOT_FOUND);
	}
}
