package ru.isin.security.endpoint.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * Util class for getting fingerprint.
 *
 * @author Bleedy (elderrat23@gmail.com) (30.03.2021)
 */
public class FingerprintUtil {
	/**
	 * Метод для добычи ip адреса.
	 *
	 * @param servletRequest данные пользователя, достаем от туда адрес
	 * @return возвращает ответ в виде HttpStatus.
	 */
	public static String getIpAdr(HttpServletRequest servletRequest) {
		if (servletRequest.getHeader("X-FORWARDED-FOR") == null) {
			return servletRequest.getRemoteAddr();
		} else {
			return servletRequest.getHeader("X-FORWARDED-FOR");
		}
	}
}
