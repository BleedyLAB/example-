package ru.isin.security.service.notification;

import org.springframework.lang.NonNull;

public interface SecurityNotification {
	void sendConfirmationRegistration(@NonNull String email,
									  @NonNull String name,
									  @NonNull String host,
									  @NonNull String port,
									  @NonNull String url,
									  @NonNull String token);

	void sendNewPasswordToOAuthUser(String email, String nameUser, String password);
}
