package ru.isin.security.service.scripts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.isin.security.domain.repository.RoleRepository;
import ru.isin.security.entities.user.PermissionEntity;
import ru.isin.security.entities.user.RoleEntity;

import java.util.List;

/**
 * Инициализатор для БД. Добавляем дефолтные права и роли.
 *
 * @author Bleedy (elderrat23@gmail.com) (30.03.2021)
 */
@Component
public class RoleInitialization implements ApplicationRunner {


	private final RoleRepository roleRepository;

	@Autowired
	public RoleInitialization(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (roleRepository.findByRole("USER").isEmpty()) {
			RoleEntity role = new RoleEntity();
			role.setRole("USER");
			role.setPermissions(List.of(new PermissionEntity("read")));
			roleRepository.save(role);
		}
		if (roleRepository.findByRole("ADMIN").isEmpty()) {
			RoleEntity role = new RoleEntity();
			role.setRole("ADMIN");
			role.setPermissions(List.of(new PermissionEntity("read"), new PermissionEntity("write")));
			roleRepository.save(role);
		}
	}
}
