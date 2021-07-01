package ru.isin.security.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.isin.core.errors.exception.AppException;
import ru.isin.core.errors.exception.BusinessException;
import ru.isin.security.service.jwt.JwtTokenProvider;
import ru.isin.security.service.notification.SecurityNotification;
import ru.isin.security.service.properties.SystemProperties;
import ru.isin.security.service.user.SecurityUserService;


/**
 * Сервис через который производится подтверждение почты.
 *
 * @author Kirill-mol (kir.mololkin@yandex.ru) (17.03.2021)
 */
@Service
@RequiredArgsConstructor
public class EmailConfirmServiceImpl implements EmailConfirmService {

	private final SecurityNotification securityNotification;
	private final SecurityUserService securityUserService;
	private final JwtTokenProvider jwtTokenProvider;
	private final SystemProperties systemProperties;

	/**
	 * Отправлка сообщения на электронную почту с ссылкой подтверждения аккаунта.
	 * Создатся токен и отправляется на почту.
	 *
	 * @param username     имя пользователя
	 * @param email        email для подтверждения
	 * @param activatorUrl url to activation controller
	 */
	@Override
	public void sendEmailConfirmation(String username, String email, String activatorUrl) {
		String token = jwtTokenProvider.createTokens(email, "USER");
		securityNotification.sendConfirmationRegistration(email, username, systemProperties.getHost(),
				systemProperties.getPort(), activatorUrl, token);
	}

	/**
	 * Активация аккаунта по jwt токену с почтой.
	 * Токен парсится с помощью функции validateTokenAndGetEmail, а затем с помощью UserService поле isEmailConfirmed
	 * обращается в true.
	 *
	 * @param token jwt токен подтверждения почты
	 * @return если не возникает ошибок возвращает true
	 * @throws AppException      возникает при валидации токена
	 * @throws BusinessException возникает в UserService, если пользователь не найден
	 */
	public Boolean activateUserAccountByToken(String token) throws AppException, BusinessException {
		jwtTokenProvider.validateToken(token);
		String email = jwtTokenProvider.getTokenSubject(token);
		securityUserService.confirmMail(email);
		return true;
	}
}
