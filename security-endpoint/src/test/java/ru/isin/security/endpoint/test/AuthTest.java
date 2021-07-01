package ru.isin.security.endpoint.test;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import ru.isin.security.domain.repository.SecurityRepository;
import ru.isin.security.endpoint.rest.AuthenticationRestController;
import ru.isin.security.service.jwt.JwtTokenProvider;

public class AuthTest {
    @Mock
    private  AuthenticationManager authenticationManager;
    @Mock
    private  SecurityRepository userRepository;
    @Mock
    private  JwtTokenProvider jwtTokenProvider;
    @InjectMocks
    private AuthenticationRestController restController;

    @Test
    void test(){
        restController.getClass();
    }


}
