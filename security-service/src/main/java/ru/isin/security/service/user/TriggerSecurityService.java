package ru.isin.security.service.user;

import ru.isin.security.entities.user.SecurityUser;

/**
 * Интерфейс триггера, который срабатывает до и после сохранения нового
 * {@link ru.isin.security.entities.user.SecurityUser} в бд.
 *
 * @author Kirill Mololkin (kir.mololkin@yandex.ru) (10.04.2021)
 */

public interface TriggerSecurityService {

	/**
	 * Методы вызывается до добавления нового {@link ru.isin.security.entities.user.SecurityUser} в бд.
	 *
	 * @param user объект который будет сохранен в бд.
	 */
	default void executeBefore(SecurityUser user) {

	}

	/**
	 * Методы вызывается после добавления нового {@link ru.isin.security.entities.user.SecurityUser} в бд.
	 *
	 * @param user объект который будет сохранен в бд.
	 */
	default void executeAfter(SecurityUser user) {

	}

}