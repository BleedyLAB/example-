package ru.isin.security.core.utils;

import java.util.Random;

/**
 * Случайный генератор для создания строки. Используем для генерации пароля и\ почты.
 *
 * @author Bleedy (elderrat23@gmail.com) (22.02.2020)
 */
public class RandomStringGenerator {
	private static final String SALT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

	/**
	 * Генерирует рандомную строку с переданной длинной.
	 *
	 * @param length длина новой строки.
	 * @return рандомная строка.
	 */
	public static String getRandomString(int length) {
		StringBuilder str = new StringBuilder();
		Random rnd = new Random();
		while (str.length() < length) { // length of the random string.
			int index = (int) (rnd.nextFloat() * SALT_CHARS.length());
			str.append(SALT_CHARS.charAt(index));
		}
		return str.toString();
	}
}
