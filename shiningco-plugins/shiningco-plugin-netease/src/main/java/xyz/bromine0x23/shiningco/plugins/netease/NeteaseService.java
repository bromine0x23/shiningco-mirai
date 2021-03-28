package xyz.bromine0x23.shiningco.plugins.netease;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

@SuppressWarnings("SameParameterValue")
@Slf4j
@Service
public class NeteaseService {

	private final RestTemplate restTemplate;

	private final ObjectMapper objectMapper;

	private final NeteaseEncryptor neteaseEncryptor = new NeteaseEncryptor();
	private final NeteaseDecryptor neteaseDecryptor = new NeteaseDecryptor();

	@SneakyThrows
	public NeteaseService(
		RestTemplateBuilder restTemplateBuilder,
		Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder
	) {
		this.restTemplate = restTemplateBuilder
			.requestFactory(OkHttp3ClientHttpRequestFactory::new)
			.build();
		this.objectMapper = jackson2ObjectMapperBuilder.build();
	}

	@Retryable
	public List<Song> search(String keyword) throws RestClientException {
		var result = cloudsearch(keyword, 30, 0);
		if (result != null) {
			return result.getSongs();
		}
		return Collections.emptyList();
	}

	private CloudsearchResult cloudsearch(String keyword, int limit, int offset) {
		var data = new LinkedHashMap<String, Object>();
		data.put("s", keyword);
		data.put("type", "1");
		data.put("limit", Integer.toString(limit));
		data.put("offset", Integer.toString(offset));
		data.put("total", "true");
		data.put("csrf_token", "");
		return request("https://music.163.com/weapi/cloudsearch/get/web?csrf_token=", data, CloudsearchResult.class);
	}

	@SneakyThrows
	private <T> T request(String url, Object data, Class<T> klass) {
		var jsonData = objectMapper.writeValueAsString(data);
		var body     = neteaseEncryptor.weapiEncrypt(jsonData);

		var headers = new HttpHeaders();
		headers.set(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.190 Safari/537.36");
		headers.set(HttpHeaders.REFERER, "https://music.163.com/");

		var json = restTemplate.postForObject(url, new HttpEntity<>(body, headers), JsonNode.class);
		return unwrapIfNeed(json, klass);
	}

	@SneakyThrows
	private <T> T unwrapIfNeed(JsonNode original, Class<T> klass) {
		if (original == null) {
			return null;
		}
		if (original.path("abroad").asBoolean()) {
			var result = original.path("result").asText();
			return objectMapper.readValue(neteaseDecryptor.abroadDecrypt(result), klass);
		}
		return objectMapper.treeToValue(original.path("result"), klass);
	}

	@Data
	public static class CloudsearchResult {

		@JsonProperty("songs")
		private List<Song> songs = new ArrayList<>();
	}

	@Data
	public static class Song {

		@JsonProperty("id")
		private long id;

		@JsonProperty("name")
		private String name;

		@JsonProperty("dt")
		private long durationMilliseconds;

		@JsonProperty("ar")
		private List<Artist> artists = new ArrayList<>();

		@JsonProperty("al")
		private Album album = new Album();
	}

	@Data
	public static class Artist {

		@JsonProperty("id")
		private long id;

		@JsonProperty("name")
		private String name;
	}

	@Data
	public static class Album {

		@JsonProperty("id")
		private long id;

		@JsonProperty("name")
		private String name;

		@JsonProperty("pic")
		private long pic;

		@JsonProperty("picUrl")
		private String picUrl;
	}


}
