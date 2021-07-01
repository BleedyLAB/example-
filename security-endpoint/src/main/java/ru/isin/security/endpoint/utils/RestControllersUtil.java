package ru.isin.security.endpoint.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.isin.security.domain.dto.responce.ErrorResponse;

import java.util.function.Supplier;

/**
 * Utils for controllers.
 *
 * @author Kirill-mol (kir.mololkin@yandex.ru) (20.03.2021)
 */
public class RestControllersUtil {

	/**
	 * Вызывет передеанную в Supplier функцию и обрабатывает возникшие значения.
	 *
	 * @param <T>      Тип возвращаемого значения
	 * @param supplier Вызов функции обернутый в Supplier
	 * @param path     Путь к контроллеру
	 * @return response entity
	 */
	public static <T> ResponseEntity handleJwtException(Supplier<T> supplier, String path) {
		try {
			supplier.get();
			return ResponseEntity.ok("success");
		} catch (Exception e) {
			var errorResponseEntity = new ErrorResponse(
					e.getMessage(),
					HttpStatus.BAD_REQUEST,
					path
			);
			return new ResponseEntity<>(errorResponseEntity, HttpStatus.NOT_FOUND);
		}
	}
}
