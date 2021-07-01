package ru.isin.security.entities.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.isin.security.entities.BaseSecurityEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Таблица для хранения ролей пользователей, у одного пользователя может быть 1 роль.
 *
 * @author Bleedy (elderrat23@gmail.com) (30.03.2021)
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "security_role")
public class RoleEntity extends BaseSecurityEntity {

	@Column(name = "role")
	private String role;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "permission_role")
	private List<PermissionEntity> permissions;
}
