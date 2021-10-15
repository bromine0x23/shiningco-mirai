package xyz.bromine0x23.shiningco.plugins.waifu;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import xyz.bromine0x23.shiningco.runtime.ProxyProperties;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
@Service
public class WaifuService {

	private static final Map<String, Set<String>> ENDPOINTS = Map.of(
		"sfw", Set.of("waifu", "neko", "shinobu", "megumin", "bully", "cuddle", "cry", "hug", "awoo", "kiss", "lick", "pat", "smug", "bonk", "yeet", "blush", "smile", "wave", "highfive", "handhold", "nom", "bite", "glomp", "slap", "kill", "kick", "happy", "wink", "poke", "dance", "cringe"),
		"nsfw", Set.of("waifu", "neko", "trap", "blowjob")
	);

	private final RestTemplate restTemplate;

	private final RateLimiter rateLimiter = RateLimiter.create(1);

	public WaifuService(
		RestTemplateBuilder restTemplateBuilder,
		ProxyProperties proxyProperties
	) {
		this.restTemplate = restTemplateBuilder
			.requestFactory(() -> createClientHttpRequestFactory(proxyProperties))
			.build();
	}

	@Retryable
	public Waifu get(boolean safeForWork, String category) {
		var type = safeForWork ? "sfw" : "nsfw";
		if (category == null || !ENDPOINTS.get(type).contains(category)) {
			category = "waifu";
		}
		return restTemplate.getForObject("https://api.waifu.pics/{type}/{category}", Waifu.class, type, category);
	}

	@Retryable
	public byte[] download(String url) {
		rateLimiter.acquire();
		return restTemplate.getForObject(url, byte[].class);
	}

	private static ClientHttpRequestFactory createClientHttpRequestFactory(ProxyProperties proxyProperties) {
		var factory = new SimpleClientHttpRequestFactory();
		if (proxyProperties.getType() != Proxy.Type.DIRECT) {
			factory.setProxy(new Proxy(proxyProperties.getType(), new InetSocketAddress(proxyProperties.getHost(), proxyProperties.getPort())));
		}
		return factory;
	}

}
