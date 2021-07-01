package ru.isin.security.service.notification;


import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.isin.starter.email.sender.EmailSender;
import ru.isin.starter.email.service.ThymeleafTemplateService;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SecurityNotificationImpl implements SecurityNotification {
	private final EmailSender emailSender;
	private final ThymeleafTemplateService thymeleafTemplateService;

	@Async
	@Override
	public void sendConfirmationRegistration(@NonNull String email,
											 @NonNull String name,
											 @NonNull String host,
											 @NonNull String port,
											 @NonNull String url,
											 @NonNull String token) {

		Map<String, Object> model = new HashMap<>();
		model.put("link", String.format("%s%s%s/%s", host, port, url, token));
		String htmlBody = thymeleafTemplateService.
				processTemplateByFilename("confirmation-email-message.html", model);
		emailSender.sendMimeMail(email, "Email confirmation message", htmlBody);

	}

	@Async
	@Override
	public void sendNewPasswordToOAuthUser(String email, String name, String password) {
		Map<String, Object> model = new HashMap<>();

		model.put("name", name);
		model.put("password", password);

		String htmlBody = thymeleafTemplateService.
				processTemplateByFilename("new-password-message.html", model);
		emailSender.sendMimeMail(email, "Your password from ISIN", htmlBody);
	}
}
