package com.anchor.global.auth;

import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.UserRole;
import com.anchor.global.auth.OAuth2UserProviderRegistry.OAuth2LoginProviderType;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuth2UserAttributes {

  private final Map<String, Object> attributes;
  private final String nameAttributeKey;
  private final String name;
  private final String email;
  private final String image;

  @Builder
  public OAuth2UserAttributes(
      Map<String, Object> attributes, String nameAttributeKey, String name, String email,
      String image
  ) {
    this.attributes = attributes;
    this.nameAttributeKey = nameAttributeKey;
    this.name = name;
    this.email = email;
    this.image = image;
  }

  @SuppressWarnings("unchecked")
  public static OAuth2UserAttributes of(
      OAuth2LoginProviderType providerType, String userNameAttributeName,
      Map<String, Object> oAuth2UserResponse
  ) {
    Map<String, Object> attributes = oAuth2UserResponse;

    if (providerType.hasAttributeField()) {
      try {
        attributes = (Map<String, Object>) oAuth2UserResponse.get(providerType.ATTRIBUTES_FIELD);
      } catch (ClassCastException e) {
        throw new IllegalArgumentException("주어진 속성 필드가 존재하지 않습니다.");
      }
    }

    return OAuth2UserAttributes.builder()
        .attributes(oAuth2UserResponse)
        .name((String) attributes.get(providerType.NAME_FIELD))
        .email((String) attributes.get(providerType.EMAIL_FIELD))
        .image((String) attributes.get(providerType.IMAGE_FIELD))
        .nameAttributeKey(userNameAttributeName)
        .build();
  }

  public User toUserEntity() {
    return User.builder()
        .nickname(name)
        .email(email)
        .image(image)
        .role(UserRole.USER)
        .build();
  }

}
