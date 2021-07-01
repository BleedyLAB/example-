package ru.isin.security.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Класс аутентификации для безопасности.
 *
 * @author Krylov Sergey (27.03.2021)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationRequest {
	private String email;
	private String password;

	public void setEmail(String email) {
		this.email = email.toLowerCase();
	}
}