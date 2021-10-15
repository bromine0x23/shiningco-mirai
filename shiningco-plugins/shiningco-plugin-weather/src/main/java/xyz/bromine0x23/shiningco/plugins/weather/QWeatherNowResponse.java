package xyz.bromine0x23.shiningco.plugins.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @see <a href="https://dev.qweather.com/docs/api/weather/weather-now/">实时天气</a>
 */
@Data
public class QWeatherNowResponse {

	@JsonProperty("code")
	private int code;

	@JsonProperty("fxLink")
	private String fxLink;

	@JsonProperty("now")
	private QWeatherNow now;

}
