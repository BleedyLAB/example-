package ru.isin.security.core.utils;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Утилита для работы с куки.
 */
public class Cookies {

	public static final String REDIRECT_URI = "REDIRECT-URI";

	public static final String REFRESH_TOKEN = "REFRESH-TOKEN";

	private Cookies() {
	}

	/**
	 * Возвращает куки по имени.
	 *
	 * @param request запрос из которого достает куки.
	 * @param name    имя куки.
	 * @return пустой объект Optional или с куки
	 */
	public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
		var cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			return Stream.of(cookies).filter(cookie -> cookie.getName().equals(name)).findAny();
		}
		return Optional.empty();
	}

	/**
	 * Добавляет куки с переданными параметрами к объекту HttpServletResponse.
	 *
	 * @param response ответ к которому добавляет куки.
	 * @param name     имя куки.
	 * @param value    значение куки.
	 * @param maxAge   максимальное время жизни куки
	 */
	public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
		var cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

	/**
	 * Удаляет очищает параметры куки по имени.
	 *
	 * @param request  запрос.
	 * @param response ответ.
	 * @param name     имя очищаемых кук
	 */
	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
		var cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (var cookie : cookies) {
				if (cookie.getName().equals(name)) {
					cookie.setValue("");
					cookie.setPath("/");
					cookie.setMaxAge(0);
					response.addCookie(cookie);
				}
			}
		}
	}
}
