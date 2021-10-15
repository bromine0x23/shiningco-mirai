package xyz.bromine0x23.shiningco.plugins.bangumi;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@Service
public class BangumiService {

	private final RestTemplate restTemplate;

	private final RateLimiter rateLimiter = RateLimiter.create(1);

	public BangumiService(
		RestTemplateBuilder restTemplateBuilder
	) {
		this.restTemplate = restTemplateBuilder
			.requestFactory(HttpComponentsClientHttpRequestFactory.class)
			.build();
	}

	public BangumiSubject show(Integer subjectId) {
		return restTemplate.getForObject("https://api.bgm.tv/subject/{subjectId}?responseGroup={responseGroup}", BangumiSubject.class, subjectId, BangumiResponseGroup.LARGE);
	}

	public List<BangumiSubject> search(String keyword, BangumiSubjectType subjectType) {
		try {
			var builder = UriComponentsBuilder.fromUriString("https://api.bgm.tv/search/subject/{keyword}");
			if (subjectType != null) {
				builder.queryParam("type", subjectType);
			}
			builder.queryParam("responseGroup", BangumiResponseGroup.SMALL);
			builder.queryParam("max_results", 25);
			var result = restTemplate.getForObject(builder.build(keyword), BangumiSearchResult.class);
			if (result != null) {
				return result.getSubjects();
			}
		} catch (HttpClientErrorException.NotFound exception) {
			// ignore
		}
		return List.of();
	}

	@Retryable
	public byte[] download(String url) {
		rateLimiter.acquire();
		return restTemplate.getForObject(url, byte[].class);
	}

}
