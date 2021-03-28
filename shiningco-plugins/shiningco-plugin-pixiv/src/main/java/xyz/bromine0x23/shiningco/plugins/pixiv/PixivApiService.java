package xyz.bromine0x23.shiningco.plugins.pixiv;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import xyz.bromine0x23.shiningco.plugins.pixiv.representations.Illustration;
import xyz.bromine0x23.shiningco.plugins.pixiv.representations.SearchIllustResult;
import xyz.bromine0x23.shiningco.plugins.pixiv.representations.SearchUserResult;
import xyz.bromine0x23.shiningco.plugins.pixiv.representations.UserPreview;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
@Slf4j
@Service
public class PixivApiService {

	private final PixivPluginProperties properties;

	private final RestTemplate restTemplate;

	private final RateLimiter rateLimiter = RateLimiter.create(1);

	private final LoadingCache<String, OAuth2AccessToken> accessTokenCache;

	public PixivApiService(
		PixivPluginProperties properties,
		PixivOAuthService oauthService,
		RestTemplateBuilder restTemplateBuilder
	) {
		this.properties       = properties;
		this.restTemplate     = restTemplateBuilder
			.requestFactory(() -> createClientHttpRequestFactory(properties.getProxy()))
			.build();
		this.accessTokenCache = CacheBuilder.newBuilder()
			.expireAfterWrite(Duration.ofSeconds(3600L))
			.build(CacheLoader.from(oauthService::refreshAccessToken));
	}

	@Retryable
	public List<Illustration> searchIllust(String keyword) {
		var searchTarget = "partial_match_for_tags";
		var sort         = "date_desc";
		var filter       = "for_ios";
		var responseEntity = restTemplate.exchange(
			"https://app-api.pixiv.net/v1/search/illust?word={keyword}&search_target={search_target}&sort={sort}&filter={filter}",
			HttpMethod.GET,
			new HttpEntity<Void>(addAuthorizationHeader(new HttpHeaders())),
			SearchIllustResult.class,
			keyword, searchTarget, sort, filter
		);
		var result = responseEntity.getBody();
		return result != null ? result.getIllustrations() : Collections.emptyList();
	}

	@Retryable
	public List<Illustration> searchIllustByUser(String keyword) {
		var sort   = "date_desc";
		var filter = "for_ios";
		var responseEntity = restTemplate.exchange(
			"https://app-api.pixiv.net/v1/search/user?word={keyword}&&sort={sort}&filter={filter}",
			HttpMethod.GET,
			new HttpEntity<Void>(addAuthorizationHeader(new HttpHeaders())),
			SearchUserResult.class,
			keyword, sort, filter
		);
		var result = responseEntity.getBody();
		if (result != null) {
			return result.getUserPreviews().stream()
				.map(UserPreview::getIllustrations)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	@Retryable
	public byte[] download(String uri) {
		rateLimiter.acquire();
		var httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.REFERER, "https://www.pixiv.net/");
		var responseEntity = restTemplate.exchange(
			uri,
			HttpMethod.GET,
			new HttpEntity<Void>(httpHeaders),
			byte[].class
		);
		return responseEntity.getBody();
	}

	private HttpHeaders addAuthorizationHeader(HttpHeaders httpHeaders) {
		httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenCache.getUnchecked(properties.getRefreshToken()).getAccessToken());
		return httpHeaders;
	}

	private static ClientHttpRequestFactory createClientHttpRequestFactory(PixivPluginProperties.ProxyProperties proxyProperties) {
		var factory = new SimpleClientHttpRequestFactory();
		if (proxyProperties.getType() != Proxy.Type.DIRECT) {
			factory.setProxy(new Proxy(proxyProperties.getType(), new InetSocketAddress(proxyProperties.getHost(), proxyProperties.getPort())));
		}
		return factory;
	}

}
