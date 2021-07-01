package ru.isin.security.core.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

/**
 * Обработчик исключений для фильтра JWT.
 *
 * @author Bleedy (elderrat23@gmail.com) (28.11.2020)
 */
@Getter
public class JwtAuthException extends AuthenticationException {
	private HttpStatus httpStatus;

	public JwtAuthException(String msg, HttpStatus httpStatus) {
		super(msg);
		this.httpStatus = httpStatus;
	}

	public JwtAuthException(String msg) {
		super(msg);
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
}
