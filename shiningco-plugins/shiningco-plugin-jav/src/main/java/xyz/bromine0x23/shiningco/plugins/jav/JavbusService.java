package xyz.bromine0x23.shiningco.plugins.jav;

import com.google.common.util.concurrent.RateLimiter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import xyz.bromine0x23.shiningco.runtime.ProxyProperties;

import java.net.InetSocketAddress;
import java.net.Proxy;

@SuppressWarnings("UnstableApiUsage")
@Service
public class JavbusService {

	private final RestTemplate restTemplate;

	private final RateLimiter rateLimiter = RateLimiter.create(1);

	public JavbusService(
		RestTemplateBuilder restTemplateBuilder,
		ProxyProperties proxyProperties
	) {
		this.restTemplate = restTemplateBuilder
			.requestFactory(() -> createClientHttpRequestFactory(proxyProperties))
			.build();
	}

	public Document search(String code) {
		try {
			var html = restTemplate.getForObject("https://www.javbus.com/{code}", String.class, code);
			if (html != null) {
				return Jsoup.parse(html, "https://www.javbus.com");
			}
		} catch (HttpClientErrorException.NotFound exception) {
			// ignore
		}
		return null;
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
