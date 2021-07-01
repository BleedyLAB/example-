package ru.isin.security.entities.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.isin.security.entities.BaseSecurityEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.HashSet;


/**
 * Таблица для хранения пользователей.
 * <p>
 * У одного пользователя может быть 1 роль в которой может быть несколько прав.
 * </p>
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "security_user")
public class SecurityUser extends BaseSecurityEntity implements UserDetails {

	@OneToOne
	@JoinColumn(name = "role")
	private RoleEntity role;

	@NotBlank
	@Column(name = "name")
	private String name;

	@Column(name = "last_name")
	private String lastName;

	@Email
	@Column(name = "email", unique = true, nullable = false)
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "is_email_confirmed")
	private boolean isEmailConfirmed;

	@Column(name = "refresh_token")
	private String refreshToken;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		var authorities = new HashSet<GrantedAuthority>();
		for (var permission : role.getPermissions()) {
			authorities.add(new SimpleGrantedAuthority(permission.getPermission()));
		}
		return authorities;
	}

	@Override
	public String getUsername() {
		return name;
	}

	@Override
	public boolean isAccountNonExpired() {
		return !getArchive();
	}

	@Override
	public boolean isAccountNonLocked() {
		return !getArchive();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return !getArchive();
	}

	@Override
	public boolean isEnabled() {
		return !getArchive();
	}
}
