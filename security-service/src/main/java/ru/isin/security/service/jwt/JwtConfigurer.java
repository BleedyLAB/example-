package ru.isin.security.service.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * Конфигурация безопасности для слоя jwt.
 *
 * @author Bleedy (elderrat23@gmail.com) (28.11.2020)
 */
@Component
@RequiredArgsConstructor
public class JwtConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
	private final JwtTokenFilter jwtTokenFilter;

	/**
	 * Создаем фильтр, передаем в него jwtFilter и определяем тип проверки (по логину и паролю).
	 *
	 * @param httpSecurity билдр, в который добавляется фильтр
	 */
	@Override
	public void configure(HttpSecurity httpSecurity) {
		httpSecurity.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
	}
}
