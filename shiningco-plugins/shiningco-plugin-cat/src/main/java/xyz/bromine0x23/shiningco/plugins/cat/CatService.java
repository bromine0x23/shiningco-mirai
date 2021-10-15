package xyz.bromine0x23.shiningco.plugins.cat;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("UnstableApiUsage")
@Service
public class CatService {

	private final RestTemplate restTemplate;

	private final RateLimiter rateLimiter = RateLimiter.create(1);

	public CatService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@Retryable
	public Meow get() {
		return restTemplate.getForObject("https://aws.random.cat/meow", Meow.class);
	}

	public byte[] download(String url) {
		rateLimiter.acquire();
		return restTemplate.getForObject(url, byte[].class);
	}

}
