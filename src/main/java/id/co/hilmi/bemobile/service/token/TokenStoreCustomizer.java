package id.co.hilmi.bemobile.service.token;

import com.fasterxml.jackson.core.type.TypeReference;
import id.co.hilmi.bemobile.service.redis.RedisManagerService;
import id.co.hilmi.bemobile.util.serializedeserialize.DeserializationUtil;
import id.co.hilmi.bemobile.util.serializedeserialize.SerializationUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static id.co.hilmi.bemobile.constant.auth.AuthorizationConstant.*;

@Slf4j
public class TokenStoreCustomizer extends TokenStoreHelper implements TokenStore {

    @Autowired
    private RedisManagerService redisManagerService;

    private ClientDetailsService clientDetailsService;

    @Value(value = "${oauth2.session.refresh}")
    private int sessionRefresh;
    @Value(value = "${oauth2.session.flush.interval}")
    private int flushInterval;
    @Value(value = "${oauth2.session.life}")
    private long sessionTimedOut;

    private final AtomicInteger flushCounter = new AtomicInteger(0);

    private final DelayQueue<TokenExpiry> expiryQueue = new DelayQueue();


    public OAuth2Authentication loadAuthentication(String tokenValue) throws AuthenticationException, InvalidTokenException {
        OAuth2AccessToken accessToken = this.readAccessToken(tokenValue);
        if (accessToken == null) {
            throw new InvalidTokenException("Invalid access token: " + tokenValue);
        } else if (accessToken.isExpired()) {
            this.removeAccessToken(accessToken);
            throw new InvalidTokenException("Access token expired: " + tokenValue);
        }

        OAuth2Authentication result = this.readAuthentication(accessToken);
        if (result == null) {
            // in case of race condition
            throw new InvalidTokenException("Invalid access token: " + tokenValue);
        }
        if (clientDetailsService != null) {
            String clientId = result.getOAuth2Request().getClientId();
            try {
                clientDetailsService.loadClientByClientId(clientId);
            } catch (ClientRegistrationException e) {
                throw new InvalidTokenException("Client not valid: " + clientId, e);
            }
        }
        return result;
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken oAuth2AccessToken) {
        return this.readAuthentication(oAuth2AccessToken.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        return (OAuth2Authentication) redisManagerService.getCache(KEY_USER_SESSION, KEY_OAUTH_AUTHENTICATION.concat(token));
    }

    @SneakyThrows
    @Override
    public void storeAccessToken(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication authentication) {
        log.info("Value token {}", flushInterval);
        if (this.flushCounter.incrementAndGet() >= this.flushInterval) {
            this.flush();
            this.flushCounter.set(0);
        }
        String secondKey = getSecondKey(authentication);
        String clientId = authentication.getOAuth2Request().getClientId();

        redisManagerService.putCache(KEY_USER_SESSION, KEY_OAUTH_ACCESS_TOKEN.concat(oAuth2AccessToken.getValue()), oAuth2AccessToken, sessionTimedOut);
        redisManagerService.putCache(KEY_USER_SESSION, KEY_OAUTH_AUTHENTICATION.concat(oAuth2AccessToken.getValue()), authentication, sessionTimedOut);

        Map<String, String> oAuthDetails = this.updateAuthDetails(oAuth2AccessToken.getValue(), authentication);
        //store authentication details to help client service getting the auth details
        redisManagerService.putCache(KEY_USER_SESSION, KEY_OAUTH_DETAILS.concat(oAuth2AccessToken.getValue()), SerializationUtil.serialize(oAuthDetails), sessionTimedOut);

        if (!authentication.isClientOnly()) {
            this.addToCollection(getApprovalKey(authentication), oAuth2AccessToken, sessionTimedOut);
        }

        this.addToCollection(getApprovalKey(clientId, secondKey), oAuth2AccessToken, sessionTimedOut);

        if (oAuth2AccessToken.getExpiration() != null) {
            var expiry = new TokenExpiry(oAuth2AccessToken.getValue(), oAuth2AccessToken.getExpiration());
            redisManagerService.putCache(KEY_USER_SESSION, KEY_OAUTH_TOKEN_EXPIRY.concat(oAuth2AccessToken.getValue()), expiry, sessionTimedOut);
        }

        if (oAuth2AccessToken.getRefreshToken() != null && oAuth2AccessToken.getRefreshToken().getValue() != null) {
            redisManagerService.putCache(KEY_USER_SESSION, KEY_OAUTH_REFRESH_TOKEN.concat(oAuth2AccessToken.getRefreshToken().getValue()), oAuth2AccessToken.getValue(), sessionTimedOut);
            redisManagerService.putCache(KEY_USER_SESSION, KEY_OAUTH_ACCESS_REFRESH_TOKEN.concat(oAuth2AccessToken.getValue()), oAuth2AccessToken.getRefreshToken().getValue(), sessionTimedOut);
        }
    }

    private Map<String, String> updateAuthDetails(String accessToken, OAuth2Authentication authentication) {
        log.info("Get latest authDetails from redis -> override value to security context");
        Object oAuthDetailsCache = redisManagerService.getCache(KEY_USER_SESSION, KEY_OAUTH_DETAILS.concat(accessToken));
        Map<String, String> oAuthDetails = authentication.getOAuth2Request().getRequestParameters();
        try {
            if (oAuthDetailsCache != null) {
                log.info("authDetails from redis is not null, update authDetails.");
                oAuthDetails = DeserializationUtil.deserializeReturnNullIfError(
                        String.valueOf(oAuthDetailsCache),
                        new TypeReference<Map<String, String>>() {}
                );
                log.debug("oAuthDetails: {}", oAuthDetails);
            }
        } catch (Exception e) {
            log.info("Unknown exception, using authDetails from security context");
        }

        return oAuthDetails;
    }

    @Override
    public OAuth2AccessToken readAccessToken(String token) {
        return (OAuth2AccessToken) redisManagerService.getCache(KEY_USER_SESSION, KEY_OAUTH_ACCESS_TOKEN.concat(token));
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken oAuth2AccessToken) {
        this.removeAccessToken(oAuth2AccessToken.getValue());
    }

    @SneakyThrows
    @Override
    public void storeRefreshToken(OAuth2RefreshToken oAuth2RefreshToken, OAuth2Authentication oAuth2Authentication) {
        redisManagerService.putCache(KEY_USER_SESSION, KEY_OAUTH_REFRESH_TOKEN.concat(oAuth2RefreshToken.getValue()), oAuth2RefreshToken, sessionRefresh);
        redisManagerService.putCache(KEY_USER_SESSION, KEY_OAUTH_AUTHENTICATION.concat(oAuth2RefreshToken.getValue()), oAuth2Authentication, sessionRefresh);
        //store authentication details to help client service getting the auth details
        redisManagerService.putCache(KEY_USER_SESSION, KEY_OAUTH_DETAILS.concat(oAuth2RefreshToken.getValue()), SerializationUtil.serialize(oAuth2Authentication.getOAuth2Request().getRequestParameters()), sessionRefresh);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String token) {
        return (OAuth2RefreshToken) redisManagerService.getCache(KEY_USER_SESSION, KEY_OAUTH_REFRESH_TOKEN.concat(token));
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {
        return this.readAuthenticationForRefreshToken(oAuth2RefreshToken.getValue());
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {
        this.removeRefreshToken(oAuth2RefreshToken.getValue());
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {
        this.removeAccessTokenUsingRefreshToken(oAuth2RefreshToken.getValue());
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication oAuth2Authentication) {
        String key = this.authenticationKeyGenerator.extractKey(oAuth2Authentication);
        String secondKey = getSecondKey(oAuth2Authentication);
        OAuth2AccessToken accessToken = (OAuth2AccessToken) redisManagerService
                .getCache(KEY_USER_SESSION, KEY_OAUTH_ACCESS_TOKEN.concat(key.concat(secondKey)));

        if (!StringUtils.isEmpty(accessToken)
                && !StringUtils.isEmpty(this.readAuthentication(accessToken.getValue()))
                && !key.equals(this.authenticationKeyGenerator.extractKey(this.readAuthentication(accessToken.getValue())))) {
            this.storeAccessToken(accessToken, oAuth2Authentication);
        }
        return accessToken;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String authId) {
        Collection<OAuth2AccessToken> result = (Collection<OAuth2AccessToken>) redisManagerService
                .getCache(KEY_USER_SESSION, KEY_OAUTH_ACCESS_TOKEN.concat(this.getApprovalKey(clientId, authId)));
        return (result != null ? Collections.unmodifiableCollection(result) : Collections.emptySet());
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        Collection<OAuth2AccessToken> result = (Collection<OAuth2AccessToken>) redisManagerService.getCache(KEY_USER_SESSION, KEY_OAUTH_ACCESS_TOKEN.concat(clientId));
        return (result != null ? Collections.unmodifiableCollection(result) : Collections.emptySet());
    }
}
