package ru.isin.security.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.isin.security.entities.user.SecurityUser;

/**
 * @author Krylov Sergey (27.03.2021)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO extends SecurityUser {
	private String name;
	private String email;
	private String password;

	public void setEmail(String email) {
		this.email = email.toLowerCase();
	}
}
