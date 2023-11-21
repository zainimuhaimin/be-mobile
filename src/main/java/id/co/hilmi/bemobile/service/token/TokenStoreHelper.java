package id.co.hilmi.bemobile.service.token;

import id.co.hilmi.bemobile.service.redis.RedisManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import static id.co.hilmi.bemobile.constant.auth.AuthorizationConstant.*;

@Slf4j
@Component
public class TokenStoreHelper {

    @Autowired
    private RedisManagerService redisManagerService;

    private final DelayQueue<TokenExpiry> expiryQueue = new DelayQueue();

    protected final AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    protected String getApprovalKey(OAuth2Authentication authentication) {
        String userName = authentication.getUserAuthentication() == null ? "" : authentication.getUserAuthentication().getName();
        return this.getApprovalKey(authentication.getOAuth2Request().getClientId(), userName);

    }

    protected String getApprovalKey(String clientId, String userName) {
        return clientId + (userName == null ? "" : ":" + userName);
    }

    protected String getSecondKey(OAuth2Authentication authentication) {
        return authentication.getOAuth2Request().getRequestParameters().get(AUTH_ID);
    }

    protected void addToCollection(String key, OAuth2AccessToken token, long sessionLife) {
        Collection<OAuth2AccessToken> store = new HashSet<>();
        store.add(token);
        redisManagerService.putCache(KEY_USER_SESSION, KEY_OAUTH_ACCESS_TOKEN.concat(key), store, sessionLife);
        log.info("Adding to Collection Oauth2AccessToken, Store [{}], Key [{}]", store, key);
    }

    protected void removeAccessToken(String tokenValue) {
        var oAuth2Authentication = (OAuth2Authentication) redisManagerService
                .getCache(KEY_USER_SESSION, KEY_OAUTH_AUTHENTICATION.concat(tokenValue));

        //removed access oauth access token
        redisManagerService.removeCache(KEY_USER_SESSION, KEY_OAUTH_ACCESS_REFRESH_TOKEN.concat(tokenValue));
        redisManagerService.removeCache(KEY_USER_SESSION, KEY_OAUTH_TOKEN_EXPIRY.concat(tokenValue));

        var secondKey = "";
        var clientId = "";

        if (null != oAuth2Authentication) {
            secondKey = getSecondKey(oAuth2Authentication);
            clientId = oAuth2Authentication.getOAuth2Request().getClientId();
            redisManagerService.removeCache(KEY_USER_SESSION, KEY_OAUTH_ACCESS_TOKEN.concat(this.authenticationKeyGenerator.extractKey(oAuth2Authentication).concat(secondKey)));
            redisManagerService.removeCache(KEY_USER_SESSION, KEY_OAUTH_ACCESS_TOKEN.concat(getApprovalKey(clientId, secondKey)));
        }

        //removed access oauth access authentication
        var oAuth2AuthenticationPersist = (OAuth2Authentication) redisManagerService.removeCachePersist(KEY_USER_SESSION, KEY_OAUTH_AUTHENTICATION.concat(tokenValue));

        if (null != oAuth2AuthenticationPersist) {
            var oAuth2AccessToken = (OAuth2AccessToken) redisManagerService
                    .removeCachePersist(KEY_USER_SESSION, KEY_OAUTH_ACCESS_TOKEN.concat(tokenValue));
            redisManagerService.removeCache(KEY_USER_SESSION, KEY_OAUTH_ACCESS_TOKEN.concat(this.authenticationKeyGenerator.extractKey(oAuth2Authentication).concat(secondKey)));
            Collection<OAuth2AccessToken> tokens = (Collection<OAuth2AccessToken>) redisManagerService.getCache(KEY_USER_SESSION, (KEY_OAUTH_ACCESS_TOKEN.concat(getApprovalKey(clientId, secondKey))));

            if (null != tokens) {
                tokens.remove(oAuth2AccessToken);
            }

            tokens = (Collection<OAuth2AccessToken>) redisManagerService.getCache(KEY_USER_SESSION, KEY_OAUTH_ACCESS_TOKEN.concat(clientId));
            if (null != tokens) {
                tokens.remove(oAuth2AccessToken);
            }
            redisManagerService.removeCache(KEY_USER_SESSION, KEY_OAUTH_ACCESS_TOKEN.concat(this.authenticationKeyGenerator.extractKey(oAuth2Authentication)));
            redisManagerService.removeCache(KEY_USER_SESSION, KEY_OAUTH_DETAILS.concat(tokenValue));
        }
    }

    protected OAuth2Authentication readAuthenticationForRefreshToken(String token) {
        return (OAuth2Authentication) redisManagerService.getCache(KEY_USER_SESSION, KEY_OAUTH_AUTHENTICATION.concat(token));
    }

    protected void removeRefreshToken(String tokenValue) {
        redisManagerService.removeCache(KEY_USER_SESSION, KEY_OAUTH_REFRESH_TOKEN.concat(tokenValue));
        redisManagerService.removeCache(KEY_USER_SESSION, KEY_OAUTH_AUTHENTICATION.concat(tokenValue));
        redisManagerService.removeCache(KEY_USER_SESSION, KEY_OAUTH_DETAILS.concat(tokenValue));
    }

    protected void removeAccessTokenUsingRefreshToken(String refreshToken) {
        String accessToken = (String) redisManagerService.removeCachePersist(KEY_USER_SESSION, KEY_OAUTH_REFRESH_TOKEN.concat(refreshToken));
        if (accessToken != null) {
            this.removeAccessToken(accessToken);
        }
    }

    protected void flush() {
        for (TokenExpiry expiry = this.expiryQueue.poll(); expiry != null; expiry = this.expiryQueue.poll()) {
            this.removeAccessToken(expiry.getValue());
        }
    }

    static class TokenExpiry implements Delayed, Serializable {
        private static final long serialVersionUID = 1614139174242022935L;
        private final long expiry;
        private final String value;

        public TokenExpiry(String value, Date date) {
            this.value = value;
            this.expiry = date.getTime();
        }

        public int compareTo(Delayed other) {
            if (this == other) {
                return 0;
            } else {
                long diff = this.getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS);
                return Long.compare(diff, 0L);
            }
        }

        public long getDelay(TimeUnit unit) {
            return this.expiry - System.currentTimeMillis();
        }

        public String getValue() {
            return this.value;
        }
    }
}
