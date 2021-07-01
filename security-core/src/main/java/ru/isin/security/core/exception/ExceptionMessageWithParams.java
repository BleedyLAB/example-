package ru.isin.security.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.isin.core.errors.MessageWithParams;

import java.util.List;

/**
 * Шаблоны сообщений об ошибках.
 */
@Getter
@AllArgsConstructor
public enum ExceptionMessageWithParams implements MessageWithParams {
	S0001("Пользователь с email <email>, не найден", List.of("email"));


	private final String messagePattern;
	private final List<String> parameters;
}
