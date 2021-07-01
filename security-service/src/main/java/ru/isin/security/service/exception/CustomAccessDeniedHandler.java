package ru.isin.security.service.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import ru.isin.security.domain.dto.responce.ErrorResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Класс отвечает за перехват несанкционированного доступа и отправляет ответ в виде json объекта.
 *
 * @author Krylov Sergey (27.03.2021)
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	/**
	 * Если в приложении появилась ошибка доступа, то отправляется ошибки в виде json.
	 *
	 * @param httpServletRequest  пришедший запрос
	 * @param httpServletResponse отправляемый ответ
	 * @param e                   ошибка доступа
	 * @throws IOException возникает при вызове метода getWriter()
	 */
	@Override
	public void handle(HttpServletRequest httpServletRequest,
					   HttpServletResponse httpServletResponse,
					   AccessDeniedException e) throws IOException {
		var mapper = new ObjectMapper();
		httpServletResponse.setContentType("application/json;charset=UTF-8");
		httpServletResponse.setStatus(403);
		httpServletResponse.getWriter().write(mapper.writeValueAsString(new ErrorResponse(
				"Access denied",
				HttpStatus.FORBIDDEN,
				httpServletRequest.getServletPath()
		)));
	}
}