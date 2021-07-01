package ru.isin.security.service.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.userdetails.UserDetails;
import ru.isin.security.domain.repository.RoleRepository;
import ru.isin.security.domain.repository.SecurityRepository;
import ru.isin.security.entities.user.PermissionEntity;
import ru.isin.security.entities.user.RoleEntity;
import ru.isin.security.entities.user.SecurityUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SecurityUserDetailServiceImplTest {
    @Mock
    private SecurityRepository securityRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private  TriggerSecurityService triggerSecurityService;
    @InjectMocks
    private SecurityUserDetailServiceImpl service;

    private SecurityUser getTestUser(){
        SecurityUser testUser = new SecurityUser();
        testUser.setName("test");
        testUser.setEmail("test");
        testUser.setPassword("test");
        return testUser;
    }

    private RoleEntity getTestUserRoleEntity(){
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRole("USER");
        roleEntity.setPermissions(List.of(new PermissionEntity("reed")));
        return roleEntity;
    }

    private RoleEntity getTestAdminRoleEntity() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRole("ADMIN");
        roleEntity.setPermissions(List.of(new PermissionEntity("reed"), new PermissionEntity("write")));
        return roleEntity;
    }

    @BeforeEach
    private void setUp(){
        when(securityRepository.findByEmail("test")).thenReturn(java.util.Optional.of(getTestUser()));
        when(securityRepository.findByName("test")).thenReturn(java.util.Optional.of(getTestUser()));
        when(roleRepository.findByRole("USER")).thenReturn(java.util.Optional.of(getTestUserRoleEntity()));
        when(roleRepository.findByRole("ADMIN")).thenReturn(java.util.Optional.of(getTestAdminRoleEntity()));

    }

    @Test
    void loadUserByUsernameTest() {
        UserDetails userDetails = service.loadUserByUsername("test");
        Assertions.assertEquals(
                UserDetails.class.getSimpleName(),
                userDetails.getClass().getInterfaces()[0].getSimpleName());
    }

    @Test
    void saveUserTest() {
        SecurityUser user = service.saveUser(getTestUser());
        assertEquals("test",user.getName());
        assertEquals("test",user.getEmail());
        assertNotEquals("test",user.getPassword());
        assertFalse(user.getArchive());
        assertEquals("USER",user.getRole().getRole());
    }

    @Test
    void saveOidcOrOauth2UserTest() {
        SecurityUser user = service.saveOidcOrOauth2User(getTestUser());
        assertEquals("test",user.getName());
        assertEquals("test",user.getEmail());
        assertNotEquals("test",user.getPassword());
        assertFalse(user.getArchive());
        assertEquals("USER",user.getRole().getRole());
        assertTrue(user.isEmailConfirmed());
    }

    @Test
    void makeAdminRoleTest() {
        SecurityUser user1 = service.saveUser(getTestUser());
        SecurityUser user2 = service.makeAdminRole(user1.getEmail());
        assertEquals("ADMIN",user2.getRole().getRole());
    }

    @Test
    void updateUserTest() {
        SecurityUser user = new SecurityUser();
        user.setPassword("123");
        user.setEmail("testtest");
        SecurityUser updatedUser = service. updateUser("test",user);
        assertEquals("testtest",updatedUser.getEmail());
        assertNotEquals("123",updatedUser.getPassword());
        assertNotEquals("test",updatedUser.getPassword());
    }

    @Test
    void confirmMailTest() {
        SecurityUser user = service.confirmMail("test");
        assertTrue(user.isEmailConfirmed());
    }
}