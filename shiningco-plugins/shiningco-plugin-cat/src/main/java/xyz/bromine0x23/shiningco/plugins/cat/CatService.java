package xyz.bromine0x23.shiningco.plugins.cat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Slf4j
@Service
public class CatService {

	private final RestTemplate restTemplate;

	private final LoadingCache<String, byte[]> imageCache = CacheBuilder.newBuilder()
		.expireAfterAccess(Duration.ofMinutes(5))
		.build(CacheLoader.from(this::fetchImage));

	public CatService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@Retryable
	@SneakyThrows
	public byte[] get() throws RestClientException {
		var meow = fetchMeow();
		if (meow != null) {
			return imageCache.get(meow.getFile());
		}
		return new byte[0];
	}

	private Meow fetchMeow() {
		return restTemplate.getForObject("https://aws.random.cat/meow", Meow.class);
	}

	private byte[] fetchImage(String uri) {
		return restTemplate.getForObject(uri, byte[].class);
	}

	@Data
	public static class Meow {

		@JsonProperty("file")
		private String file;

	}

}
