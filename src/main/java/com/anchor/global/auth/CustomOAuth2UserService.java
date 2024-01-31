package com.anchor.global.auth;

import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.repository.UserRepository;
import com.anchor.global.auth.OAuth2UserProviderRegistry.OAuth2LoginProviderType;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final UserRepository userRepository;
  private final HttpSession httpSession;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserLoadProcessor = new DefaultOAuth2UserService();
    OAuth2User oAuth2User = oauth2UserLoadProcessor.loadUser(userRequest);
    String registrationId = userRequest.getClientRegistration()
        .getRegistrationId();

    OAuth2LoginProviderType providerType = OAuth2UserProviderRegistry.getType(registrationId);
    String userNameAttributeName = userRequest.getClientRegistration()
        .getProviderDetails()
        .getUserInfoEndpoint()
        .getUserNameAttributeName();
    Map<String, Object> oAuth2UserResponse = oAuth2User.getAttributes();
    OAuth2UserAttributes oAuth2UserAttributes = OAuth2UserAttributes.of(providerType,
        userNameAttributeName, oAuth2UserResponse);

    User user = save(oAuth2UserAttributes);
    httpSession.setAttribute("user", new SessionUser(user));

    return new DefaultOAuth2User(
        Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
        oAuth2UserAttributes.getAttributes(),
        oAuth2UserAttributes.getNameAttributeKey()
    );
  }

  private User save(OAuth2UserAttributes oAuth2UserAttributes) {
    User user = userRepository.findByEmail(oAuth2UserAttributes.getEmail())
        .orElse(oAuth2UserAttributes.toUserEntity());

    return userRepository.save(user);
  }

}
