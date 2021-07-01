package ru.isin.security.service.user;

import lombok.extern.slf4j.Slf4j;
import ru.isin.security.entities.user.SecurityUser;

@Slf4j
public class TriggerSecurityServiceSimpleImpl implements TriggerSecurityService{
	@Override
	public void executeBefore(SecurityUser user) {
		log.info("Base executeBefore method in TriggerSecurityService called");
	}

	@Override
	public void executeAfter(SecurityUser user) {
		log.info("Base executeAfter method in TriggerSecurityService called");
	}
}
