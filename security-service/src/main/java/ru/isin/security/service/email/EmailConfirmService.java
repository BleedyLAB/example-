package ru.isin.security.service.email;

import ru.isin.core.errors.exception.AppException;
import ru.isin.core.errors.exception.BusinessException;

/**
 * Интерфейс сервиса через который производится подтверждение почты.
 *
 * @author Kirill-mol (kir.mololkin@yandex.ru) (17.03.2021)
 */
public interface EmailConfirmService {
	void sendEmailConfirmation(String username, String email, String path);

	Boolean activateUserAccountByToken(String token) throws AppException, BusinessException;
}
