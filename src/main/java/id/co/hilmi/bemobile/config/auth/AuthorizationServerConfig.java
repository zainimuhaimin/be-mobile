package id.co.hilmi.bemobile.config.auth;

import id.co.hilmi.bemobile.exception.auth.CustomResponseExceptionTranslator;
import id.co.hilmi.bemobile.service.token.TokenStoreCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.StringUtils;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;

    @Autowired
    private TokenStore tokenStore;

    @Value("${oauth2.clientId}")
    private String oauth2ClientId;

    @Value("${oauth2.clientSecret}")
    private String oauth2ClientSecret;

    @Value("${oauth2.session.life}")
    private String oauth2TokenTimeToLive;

    PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.authenticationManager(authenticationManager)
                .exceptionTranslator(new CustomResponseExceptionTranslator())
                .tokenStore(tokenStoreCustomizer())
        ;
    }


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.allowFormAuthenticationForClients()
                .passwordEncoder(passwordEncoder)
                .checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        var oauth2AccessTokenTimeoutSec = oauth2TokenTimeToLive;

        int tokenTimeout = StringUtils.isEmpty(oauth2AccessTokenTimeoutSec) ? 300 :
                Integer.parseInt(oauth2AccessTokenTimeoutSec);

        clients.inMemory()
                .withClient(oauth2ClientId)
                .authorizedGrantTypes("client_credentials")
                .scopes("read", "write", "trust")
                .secret(passwordEncoder.encode(oauth2ClientSecret))
                .accessTokenValiditySeconds(tokenTimeout);

    }

    @Bean
    @Primary
    public TokenStoreCustomizer tokenStoreCustomizer() {
        return new TokenStoreCustomizer();
    }
}
