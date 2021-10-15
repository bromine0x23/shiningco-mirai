package xyz.bromine0x23.shiningco.plugins.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * @see <a href="https://dev.qweather.com/docs/api/weather/weather-now/">实时天气</a>
 */
@Data
public class QWeatherNow {

	@JsonProperty("obsTime")
	private String obsTime;

	@JsonProperty("temp")
	private String temperature;

	@JsonProperty("text")
	private String text;

}
