package ru.isin.security.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.isin.core.errors.BusinessExceptionReason;
import ru.isin.core.errors.MessageWithParams;
import ru.isin.core.errors.domain.ErrorType;

/**
 * Реализация {@link BusinessExceptionReason}.
 * Список ошибок.
 */
@Getter
@AllArgsConstructor
public enum SecurityExceptionReason implements BusinessExceptionReason {
	// Бизнес-ошибки - E

	// Информационные ошибки - I

	/**
	 * Ошибки целостности данных - D.
	 */
	USER_NOT_FOUND_BY_EMAIL(ErrorType.INFO, "S0001", ExceptionMessageWithParams.S0001);


	private final ErrorType errorType;
	private final String code;
	private final MessageWithParams messageWithParams;
}
