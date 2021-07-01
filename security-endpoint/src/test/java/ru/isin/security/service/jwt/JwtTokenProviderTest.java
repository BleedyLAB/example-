package ru.isin.security.service.jwt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.isin.security.domain.repository.SecurityRepository;
import ru.isin.security.service.properties.JwtProperties;
import ru.isin.security.service.user.SecurityUserService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtTokenProviderTest {
    @Mock
    private  SecurityUserService securityUserService;
    @Mock
    private  SecurityRepository repository;
    @Mock
    private  JwtProperties jwtProperties;
    @InjectMocks
    private JwtTokenProvider tokenProvider;

    @Test
    void createTokensTest() {
    }

    @Test
    void createRefreshTokenTest() {
    }

    @Test
    void validateTokenTest() {
    }

    @Test
    void getAuthenticationTest() {
    }

    @Test
    void getTokenSubjectTest() {
    }

    @Test
    void getDataFromTokenBodyTest() {
    }

    @Test
    void putRefreshTokenTest() {
    }

    @Test
    void resolveTokenTest() {
    }
}