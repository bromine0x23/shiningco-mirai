package xyz.bromine0x23.shiningco.plugins.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://dev.qweather.com/docs/api/weather/weather-daily-forecast/">逐天天气预报</a>
 */
@Data
public class QWeatherDailyResponse {

	@JsonProperty("code")
	private int code;

	@JsonProperty("fxLink")
	private String fxLink;

	@JsonProperty("daily")
	private List<QWeatherDaily> daily = new ArrayList<>();

}
