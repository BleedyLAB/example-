package ru.isin.security.entities.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.isin.security.entities.BaseSecurityEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Таблица для хранения прав пользователей.
 *
 * @author Bleedy (elderrat23@gmail.com) (30.03.2021)
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "security_permision")
public class PermissionEntity extends BaseSecurityEntity {

	private String permission;
}
