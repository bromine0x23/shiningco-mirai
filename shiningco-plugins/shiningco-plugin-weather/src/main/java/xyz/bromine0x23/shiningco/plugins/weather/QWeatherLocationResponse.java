package xyz.bromine0x23.shiningco.plugins.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://dev.qweather.com/docs/api/geo/city-lookup/">城市信息查询</a>
 */
@Data
public class QWeatherLocationResponse {

	@JsonProperty("code")
	private int code;

	@JsonProperty("fxLink")
	private String fxLink;

	@JsonProperty("location")
	private List<QWeatherLocation> location = new ArrayList<>();

}
