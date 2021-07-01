package ru.isin.security.domain.dto.oauth;

import lombok.Data;

/**
 * DTO для пользователей google.
 *
 * @author Bleedy (elderrat23@gmail.com) (22.02.2020)
 */
@Data
public class GoogleOAuth2UserInfo {
	private String id;
	private String name;
	private String email;
	private String imageUrl;
}