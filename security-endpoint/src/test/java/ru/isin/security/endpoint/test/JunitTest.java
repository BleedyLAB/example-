package ru.isin.security.endpoint.test;



import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.isin.security.domain.repository.SecurityRepository;
import ru.isin.security.entities.user.SecurityUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class JunitTest {

    @Mock
    private SecurityRepository securityRepository;


    @BeforeAll
    public void setup(){
    SecurityUser user = new SecurityUser();
        user.setName("123");

    }


    @Test
    @DisplayName("DB test")
    void someTest(){
        when(securityRepository.findByName("123")).thenReturn(Optional.of(new SecurityUser()));
        Optional<SecurityUser> user1 =  securityRepository.findByName("123");
        SecurityUser user2 =  user1.get();
        user2.setName("123");
        assertEquals(user2.getName(),"123");
    }

    @Test
    @DisplayName("trash test")
    void some(){
        assertEquals(1,1);
    }

}