package xyz.bromine0x23.shiningco.plugins.pixiv;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.httpclient.jdk.JDKHttpClient;
import com.github.scribejava.core.httpclient.jdk.JDKHttpClientConfig;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.oauth2.clientauthentication.ClientAuthentication;
import com.github.scribejava.core.oauth2.clientauthentication.RequestBodyAuthenticationScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import xyz.bromine0x23.shiningco.runtime.ProxyProperties;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Slf4j
@Service
public class PixivOAuthService {

	private final OAuth20Service oauthService;

	public PixivOAuthService(
		PixivPluginProperties properties,
		ProxyProperties proxyProperties
	) {
		this.oauthService = new ServiceBuilder(properties.getApiKey())
			.apiSecret(properties.getApiSecret())
			.httpClient(new JDKHttpClient(new JDKHttpClientConfig().withProxy(getProxy(proxyProperties))))
			.userAgent("PixivAndroidApp/5.0.115 (Android 9.0; ShiningCo)")
			.build(new PixivOAuthApi());
	}

	@Retryable
	public OAuth2AccessToken refreshAccessToken(String refreshToken) {
		try {
			var accessToken = oauthService.refreshAccessToken(refreshToken);
			log.info("Pixiv access-token: {} (refresh by {})", accessToken.getAccessToken(), refreshToken);
			return accessToken;
		} catch (Exception exception) {
			log.debug("Error on init: ", exception);
		}
		return null;
	}

	public static class PixivOAuthApi extends DefaultApi20 {

		@Override
		public ClientAuthentication getClientAuthentication() {
			return RequestBodyAuthenticationScheme.instance();
		}

		@Override
		public String getAccessTokenEndpoint() {
			return "https://oauth.secure.pixiv.net/auth/token";
		}

		@Override
		protected String getAuthorizationBaseUrl() {
			return "https://oauth.secure.pixiv.net/auth/authorization";
		}

	}

	private static Proxy getProxy(ProxyProperties proxyProperties) {
		if (proxyProperties.getType() != Proxy.Type.DIRECT) {
			return new Proxy(proxyProperties.getType(), new InetSocketAddress(proxyProperties.getHost(), proxyProperties.getPort()));
		}
		return null;
	}

}
