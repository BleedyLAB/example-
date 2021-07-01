package ru.isin.security.service.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.lang.NonNull;

/**
 * Проперти класс для jwt.
 *
 * @author Kirill-mol (kir.mololkin@yandex.ru) (17.03.2021)
 */
@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "isin.security.jwt")
public class JwtProperties {
	@NonNull
	private final String header;

	@NonNull
	private final String refreshTokenHeader;

	@NonNull
	private final String secret;

	private final Integer expirationToken;

	private final Integer expirationRefreshToken;

	@NonNull
	private final String redirectURL;

	/**
	 * Конструктор проперти класса.
	 *
	 * @param header хедер jwt токена
	 * @param refreshTokenHeader хедер рефреш токена
	 * @param secret сектретная строка кодирования
	 * @param expirationToken время дейтсвия токена
	 * @param expirationRefreshToken время действия рефреш токена
	 * @param redirectURL сслыка редиректа после успешной аутентификации
	 */
	public JwtProperties(@NonNull String header,
						 @NonNull String refreshTokenHeader,
						 @NonNull String secret,
						 Integer expirationToken,
						 Integer expirationRefreshToken,
						 @NonNull String redirectURL) {
		this.header = header;
		this.refreshTokenHeader = refreshTokenHeader;
		this.secret = secret;
		this.expirationToken = expirationToken;
		this.expirationRefreshToken = expirationRefreshToken;
		this.redirectURL = redirectURL;
	}
}
