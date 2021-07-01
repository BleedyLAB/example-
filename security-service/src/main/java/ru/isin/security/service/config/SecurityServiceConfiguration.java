package ru.isin.security.service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import ru.isin.security.core.domain.Constants;
import ru.isin.security.domain.repository.RoleRepository;
import ru.isin.security.domain.repository.SecurityRepository;
import ru.isin.security.service.OAuth2.AuthenticationFailureHandlerImp;
import ru.isin.security.service.OAuth2.AuthenticationSuccessHandlerImpl;
import ru.isin.security.service.OAuth2.HttpCookieOAuth2AuthorizationRequestRepository;
import ru.isin.security.service.email.EmailConfirmService;
import ru.isin.security.service.email.EmailConfirmServiceImpl;
import ru.isin.security.service.exception.CustomAccessDeniedHandler;
import ru.isin.security.service.jwt.JwtConfigurer;
import ru.isin.security.service.jwt.JwtTokenFilter;
import ru.isin.security.service.jwt.JwtTokenProvider;
import ru.isin.security.service.notification.SecurityNotification;
import ru.isin.security.service.notification.SecurityNotificationImpl;
import ru.isin.security.service.properties.JwtProperties;
import ru.isin.security.service.properties.SystemProperties;
import ru.isin.security.service.scripts.RoleInitialization;
import ru.isin.security.service.user.OidcUserServiceImpl;
import ru.isin.security.service.user.SecurityUserService;
import ru.isin.security.service.user.TriggerSecurityServiceSimpleImpl;
import ru.isin.security.service.user.TriggerSecurityService;
import ru.isin.security.service.user.SecurityUserDetailServiceImpl;
import ru.isin.security.service.user.OAuth2UserServiceImpl;

import ru.isin.starter.email.config.EmailProperties;
import ru.isin.starter.email.sender.EmailSender;
import ru.isin.starter.email.service.ThymeleafTemplateService;
import ru.isin.starter.email.service.ThymeleafTemplateServiceImpl;

/**
 * Конфигурация модуля Security Service.
 *
 * @author Bleedy (elderrat23@gmail.com)
 */
@Slf4j
@EnableAsync
@Configuration
@EnableConfigurationProperties({JwtProperties.class, SystemProperties.class})
public class SecurityServiceConfiguration {
    @Bean
    @ConditionalOnMissingBean(EmailConfirmService.class)
    public EmailConfirmService emailConfirmService(SecurityNotification securityNotification,
                                                   SecurityUserService securityUserService,
                                                   JwtTokenProvider jwtTokenProvider,
                                                   SystemProperties systemProperties) {
        log.info("ISIN: Security {} -> Initialization Bean: EmailConfirmService", Constants.VERSION);
        return new EmailConfirmServiceImpl(securityNotification, securityUserService, jwtTokenProvider, systemProperties);
    }

    @Bean
    @ConditionalOnMissingBean(RoleInitialization.class)
    public RoleInitialization roleInitialization(RoleRepository roleRepository) {
        log.info("ISIN: Security {} -> Initialization Bean: RoleInitialization", Constants.VERSION);
        return new RoleInitialization(roleRepository);
    }

    @Bean
    @ConditionalOnMissingBean(OidcUserService.class)
    public OidcUserService oidcUserService(RoleRepository roleRepository,
                                           SecurityUserService securityUserService,
                                           SecurityNotification securityNotification) {
        log.info("ISIN: Security {} -> Initialization Bean: OidcUserService", Constants.VERSION);
        return new OidcUserServiceImpl(roleRepository, securityUserService, securityNotification);
    }

    @Bean
    @ConditionalOnMissingBean(SecurityNotification.class)
    public SecurityNotification securityNotification(EmailSender emailSender,
                                                     ThymeleafTemplateService thymeleafTemplateService) {
        log.info("ISIN: Security {} -> Initialization Bean: SecurityNotification", Constants.VERSION);
        return new SecurityNotificationImpl(emailSender, thymeleafTemplateService);
    }

	@Bean
	@ConditionalOnMissingBean(TriggerSecurityService.class)
	public TriggerSecurityService triggerSecurityService() {
		log.info("ISIN: Security {} -> Initialization Bean: TriggerSecurityService", Constants.VERSION);
		return new TriggerSecurityServiceSimpleImpl();
	}

    @Bean
    @ConditionalOnMissingBean(DefaultOAuth2UserService.class)
    public DefaultOAuth2UserService defaultOAuth2UserService(SecurityUserService securityUserService,
                                                             RoleRepository roleRepository) {
        log.info("ISIN: Security {} -> Initialization Bean: DefaultOAuth2UserService", Constants.VERSION);
        return new OAuth2UserServiceImpl(securityUserService, roleRepository);
    }

    @Bean
    @ConditionalOnMissingBean(HttpCookieOAuth2AuthorizationRequestRepository.class)
    public HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository(
            JwtProperties jwtProperties) {
        log.info("ISIN: Security {} -> Initialization Bean: HttpCookieOAuth2AuthorizationRequestRepository", Constants.VERSION);
        return new HttpCookieOAuth2AuthorizationRequestRepository(jwtProperties);
    }

    @Bean
    @ConditionalOnMissingBean(SimpleUrlAuthenticationSuccessHandler.class)
    public SimpleUrlAuthenticationSuccessHandler simpleUrlAuthenticationSuccessHandler(
            JwtProperties jwtProperties,
            JwtTokenProvider tokenProvider,
            HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository,
            SecurityUserService securityUserService) {
        log.info("ISIN: Security {} -> Initialization Bean: AuthenticationFailureHandler", Constants.VERSION);
        return new AuthenticationSuccessHandlerImpl(jwtProperties, tokenProvider,
                authorizationRequestRepository, securityUserService);
    }


    @Bean
    @ConditionalOnClass(HttpCookieOAuth2AuthorizationRequestRepository.class)
    @ConditionalOnMissingBean(AuthenticationFailureHandler.class)
    public AuthenticationFailureHandler authenticationFailureHandler(
            @Lazy HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository) {
        log.info("ISIN: Security {} -> Initialization Bean: AuthenticationFailureHandler", Constants.VERSION);
        return new AuthenticationFailureHandlerImp(httpCookieOAuth2AuthorizationRequestRepository);
    }

    @Bean
    @ConditionalOnMissingBean(SecurityUserService.class)
    public SecurityUserService securityUserService(SecurityRepository securityRepository,
                                                   RoleRepository roleRepository,
												   TriggerSecurityService triggerSecurityService) {
        log.info("ISIN: Security {} -> Initialization Bean: SecurityUserService", Constants.VERSION);
        return new SecurityUserDetailServiceImpl(securityRepository, roleRepository, triggerSecurityService);
    }

    @Bean
    @ConditionalOnMissingBean(JwtTokenProvider.class)
    public JwtTokenProvider jwtTokenProvider(SecurityUserService securityUserService,
                                             SecurityRepository securityRepository,
                                             JwtProperties jwtProperties) {
        log.info("ISIN: Security {} -> Initialization Bean: JwtTokenProvider", Constants.VERSION);
        return new JwtTokenProvider(securityUserService, securityRepository, jwtProperties);
    }

    @Bean
    @ConditionalOnMissingBean(JwtTokenFilter.class)
    public JwtTokenFilter jwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        log.info("ISIN: Security {} -> Initialization Bean: JwtTokenFilter", Constants.VERSION);
        return new JwtTokenFilter(jwtTokenProvider);
    }

    @Bean
    @ConditionalOnMissingBean(JwtConfigurer.class)
    public JwtConfigurer jwtConfigurer(JwtTokenFilter jwtTokenFilter) {
        log.info("ISIN: Security {} -> Initialization Bean: JwtConfigurer", Constants.VERSION);
        return new JwtConfigurer(jwtTokenFilter);
    }

    @Bean
    @ConditionalOnMissingBean(AccessDeniedHandler.class)
    public AccessDeniedHandler accessDeniedHandler() {
        log.info("ISIN: Security {} -> Initialization Bean: AccessDeniedHandler", Constants.VERSION);
        return new CustomAccessDeniedHandler();
    }

    @Bean
    @ConditionalOnMissingBean(ThymeleafTemplateService.class)
    public ThymeleafTemplateService thymeleafTemplateService(EmailProperties emailProperties) {
        log.info("ISIN: Security {} -> Initialization Bean: ThymeleafTemplateService", Constants.VERSION);
        return new ThymeleafTemplateServiceImpl(emailProperties);
    }


}
