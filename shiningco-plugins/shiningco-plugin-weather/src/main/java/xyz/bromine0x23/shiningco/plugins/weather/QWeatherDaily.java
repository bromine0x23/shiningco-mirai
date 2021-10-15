package xyz.bromine0x23.shiningco.plugins.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @see <a href="https://dev.qweather.com/docs/api/weather/weather-daily-forecast/">逐天天气预报</a>
 */
@Data
public class QWeatherDaily {

	@JsonProperty("fxDate")
	private String fxDate;

	@JsonProperty("tempMax")
	private String temperatureMax;

	@JsonProperty("tempMin")
	private String temperatureMin;

	@JsonProperty("textDay")
	private String textDay;

	@JsonProperty("textNight")
	private String textNight;

}
