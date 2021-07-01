package ru.isin.security.domain.dto.responce;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO для отправки данных при authentication.
 *
 * @author Krylov Sergey (27.03.2021)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuthenticationResponse extends BaseResponse {
	private String token;

	private String refreshToken;
}
