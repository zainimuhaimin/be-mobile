package id.co.hilmi.bemobile.service.token;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

import static id.co.hilmi.bemobile.constant.auth.AuthorizationConstant.AUTH_ID;

@Slf4j
@Service
public class TokenService {

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @Value("${oauth2.clientId}")
    private String oauth2ClientId;

    @Value("${oauth2.clientSecret}")
    private String oauth2ClientSecret;

    @SneakyThrows
    public Map<String, String> constructAccessTokenParameters(String userId) {
        HashMap<String, String> parameters = new HashMap<>();

        // tambahkan parameters userDetails sedetail mungkin dan kebutuhan lainnya jika di perlukan
        // parameters di bawah akan di put ke cache
        parameters.put("client_id", oauth2ClientId);
        parameters.put("client_secret", oauth2ClientSecret);
        parameters.put("grant_type", "client_credentials");
        parameters.put(AUTH_ID, userId);

        return parameters;
    }

    public OAuth2AccessToken getAccessToken(Map<String, String> accessTokenParameters) throws HttpRequestMethodNotSupportedException {
        Collection<GrantedAuthority> authorityCollection = new ConcurrentLinkedDeque<>();
        var userPrincipal = new User(oauth2ClientId, oauth2ClientSecret, true, true, true, true, authorityCollection);
        var principal = new UsernamePasswordAuthenticationToken(userPrincipal, oauth2ClientSecret, authorityCollection);

        ResponseEntity<OAuth2AccessToken> accessToken = tokenEndpoint.postAccessToken(principal, accessTokenParameters);
        log.debug(accessTokenParameters.toString());
        return accessToken.getBody();
    }

    @SneakyThrows
    public OAuth2AccessToken getLoginSessionToken(String username) {
        Map<String, String> tokenParameters = this.constructAccessTokenParameters(username);
        return this.getAccessToken(tokenParameters);
    }
}
