package com.anchor.global.auth;

import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public abstract class OAuth2UserProviderRegistry {

  private static final Set<OAuth2LoginProviderType> oAuth2LoginProviderTypes = new HashSet<>();

  static {
    oAuth2LoginProviderTypes.add(OAuth2LoginProviderType.GOOGLE);
    oAuth2LoginProviderTypes.add(OAuth2LoginProviderType.NAVER);
  }

  public static OAuth2LoginProviderType getType(String registrationId) {
    return oAuth2LoginProviderTypes.stream()
        .filter(oAuth2LoginProviderType ->
            oAuth2LoginProviderType.isEqualTo(registrationId))
        .findAny()
        .orElseThrow(
            () -> new IllegalArgumentException("주어진 registrationId와 일치하는 Provider가 없습니다."));
  }

  public enum OAuth2LoginProviderType {
    GOOGLE("google", null, "name", "email", "picture"),
    NAVER("naver", "response", "name", "email", "profile_image");

    public final String REGISTRATION_ID;
    public final String ATTRIBUTES_FIELD;
    public final String NAME_FIELD;
    public final String EMAIL_FIELD;
    public final String IMAGE_FIELD;

    OAuth2LoginProviderType(String REGISTRATION_ID, String ATTRIBUTES_FIELD, String NAME_FIELD,
        String EMAIL_FIELD, String IMAGE_FIELD) {
      this.REGISTRATION_ID = REGISTRATION_ID;
      this.ATTRIBUTES_FIELD = ATTRIBUTES_FIELD;
      this.NAME_FIELD = NAME_FIELD;
      this.EMAIL_FIELD = EMAIL_FIELD;
      this.IMAGE_FIELD = IMAGE_FIELD;
    }

    private boolean isEqualTo(String registrationId) {
      return REGISTRATION_ID.equals(registrationId);
    }

    public boolean hasAttributeField() {
      return ATTRIBUTES_FIELD != null;
    }

  }

}
