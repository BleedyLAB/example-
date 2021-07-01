package ru.isin.security.domain.dto.oauth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import ru.isin.security.entities.user.SecurityUser;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyMap;

/**
 * DTO для пользователей входящих через OAuth.
 *
 * @author Bleedy (elderrat23@gmail.com) (22.02.2020)
 */
public class OAuth2ToUser implements OAuth2User, UserDetails {
	private final Long id;

	private final String name;

	private final Map<String, Object> attributes;

	private final Set<GrantedAuthority> authorities;

	/**
	 * Создает OAuth2ToUser на базе user без атрибутов.
	 *
	 * @param securityUser пользователь
	 */
	public OAuth2ToUser(SecurityUser securityUser) {
		this(securityUser, emptyMap());
	}

	/**
	 * Создает OAuth2ToUser на базе user с переданными атрибутами и дефолтной ролью.
	 *
	 * @param securityUser пользователь
	 * @param attributes   атрибуты нового OAuth2ToUser объекта
	 */
	public OAuth2ToUser(SecurityUser securityUser, Map<String, Object> attributes) {
		this.id = securityUser.getId();
		this.name = securityUser.getName();
		this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
		this.authorities = (Set<GrantedAuthority>) securityUser.getAuthorities();
	}

	public Long getId() {
		return id;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return name;
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Id: [" +
				getId() +
				"], Name: [" +
				getName() +
				"], Granted Authorities: [" +
				getAuthorities() +
				"], User Attributes: [" +
				getAttributes() +
				"]";
	}
}
