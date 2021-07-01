package ru.isin.security.endpoint.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import ru.isin.security.domain.repository.SecurityRepository;
import ru.isin.security.endpoint.rest.AuthenticationRestController;
import ru.isin.security.endpoint.rest.OAuthController;
import ru.isin.security.endpoint.rest.RegisterController;
import ru.isin.security.endpoint.rest.TestAuthController;
import ru.isin.security.core.domain.Constants;
import ru.isin.security.service.jwt.JwtTokenProvider;
import ru.isin.security.service.user.SecurityUserService;
import ru.isin.security.service.email.EmailConfirmService;

/**
 * Конфигурация модуля Isinka Endpoint.
 *
 * @author Krylov Sergey (09.01.2021)
 */
@Slf4j
@Configuration
public class SecurityEndpointConfiguration {
	@Bean
	@ConditionalOnMissingBean(AuthenticationRestController.class)
	public AuthenticationRestController authenticationRestController(
			AuthenticationManager authenticationManager,
			SecurityRepository userRepository,
			JwtTokenProvider jwtTokenProvider) {
		log.info("ISIN: Security {} -> Initialization Bean: AuthenticationRestController", Constants.VERSION);
		return new AuthenticationRestController(authenticationManager, userRepository, jwtTokenProvider);
	}

	@Bean
	@ConditionalOnMissingBean(OAuthController.class)
	public OAuthController oAuthController(SecurityRepository userRepository,
										   JwtTokenProvider jwtTokenProvider) {
		log.info("ISIN: Security {} -> Initialization Bean: OAuthController", Constants.VERSION);
		return new OAuthController(userRepository, jwtTokenProvider);
	}

	@Bean
	@ConditionalOnMissingBean(RegisterController.class)
	public RegisterController registerController(SecurityUserService securityUserService,
												 JwtTokenProvider tokenProvider,
												 EmailConfirmService emailConfirmService) {
		log.info("ISIN: Security {} -> Initialization Bean: RegisterController", Constants.VERSION);
		return new RegisterController(securityUserService, tokenProvider, emailConfirmService);
	}

	@Bean
	@ConditionalOnMissingBean(TestAuthController.class)
	public TestAuthController testAuthController() {
		log.info("ISIN: Security {} -> Initialization Bean: TestAuthController", Constants.VERSION);
		return new TestAuthController();
	}
}
