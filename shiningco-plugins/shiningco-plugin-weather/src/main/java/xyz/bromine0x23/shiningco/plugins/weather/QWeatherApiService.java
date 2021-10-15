package xyz.bromine0x23.shiningco.plugins.weather;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class QWeatherApiService {

	private final RestTemplate restTemplate;

	public QWeatherApiService(
		RestTemplateBuilder restTemplateBuilder
	) {
		this.restTemplate = restTemplateBuilder.build();
	}

	public List<QWeatherLocation> locations(String locationKey, String key) {
		var response = restTemplate.getForObject("https://geoapi.qweather.com/v2/city/lookup?location={location}&key={key}", QWeatherLocationResponse.class, locationKey, key);
		return Objects.requireNonNull(response).getLocation();
	}

	public QWeatherNow now(String locationId, String key) {
		var response = restTemplate.getForObject("https://devapi.qweather.com/v7/weather/now?location={locationId}&key={key}", QWeatherNowResponse.class, locationId, key);
		return Objects.requireNonNull(response).getNow();
	}

	public List<QWeatherDaily> latest3d(String locationId, String key) {
		var response = restTemplate.getForObject("https://devapi.qweather.com/v7/weather/3d?location={locationId}&key={key}", QWeatherDailyResponse.class, locationId, key);
		return Objects.requireNonNull(response).getDaily();
	}

}
