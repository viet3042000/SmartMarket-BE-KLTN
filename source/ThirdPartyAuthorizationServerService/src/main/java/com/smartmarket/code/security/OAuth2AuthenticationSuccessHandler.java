package com.smartmarket.code.security;

import com.smartmarket.code.util.CookieUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Optional;


@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Autowired
    CustomOAuth2UserService customOAuth2UserService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
        String targetUrl = determineTargetUrl(request, response, authentication,oauthUser);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }


    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication, CustomOAuth2User oauthUser) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, httpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElse("/home");

        //SET TIMEOUT
        SimpleClientHttpRequestFactory clientHttpRequestFactoryCreateBIC = new SimpleClientHttpRequestFactory();
        //Connect timeout
        clientHttpRequestFactoryCreateBIC.setConnectTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.createTravelBIC")));
        //Read timeout
        clientHttpRequestFactoryCreateBIC.setReadTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.createTravelBIC")));

        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactoryCreateBIC);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://103.9.0.239:31441/oauth/token/")
                .queryParam("grant_type", "password")
                .queryParam("username", "hung")
                .queryParam("password", "hung123");

        HttpEntity httpEntity = new HttpEntity<>(this.createHeaders());

        ResponseEntity<String> gettoken = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, httpEntity, String.class);
        JSONObject jsonObject = new JSONObject(gettoken.getBody());
        String accessToken = jsonObject.getString("access_token");

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", accessToken)
                .build().toUriString();
    }


    protected HttpHeaders createHeaders(){
        return new HttpHeaders() {{
            String auth = "client3" + ":" + "client3";
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII")) );
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
        }};
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        // Only validate host and port. Let the clients use different paths if they want to
        URI authorizedURI = URI.create("http://localhost:8085/oauth2/redirect");
        if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                && authorizedURI.getPort() == clientRedirectUri.getPort()) {
            return true;
        }
        return false;
    }
}