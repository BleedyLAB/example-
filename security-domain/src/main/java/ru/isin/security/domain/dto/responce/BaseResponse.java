package ru.isin.security.domain.dto.responce;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseResponse {
	private String message;

	private HttpStatus httpStatus;

	public BaseResponse(String message) {
		this.message = message;
		httpStatus = HttpStatus.OK;
	}
}

