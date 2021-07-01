package ru.isin.security.endpoint.rest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestAuthController {
	@GetMapping("/test")
	@PreAuthorize("hasAuthority('read')")
	public String mainPage() {
		return "Isin Platform Test is done!";
	}
}