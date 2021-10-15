package xyz.bromine0x23.shiningco.plugins.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @see <a href="https://dev.qweather.com/docs/api/geo/city-lookup/">城市信息查询</a>
 */
@Data
public class QWeatherLocation {

	@JsonProperty("id")
	private String id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("adm2")
	private String adm2;

	@JsonProperty("adm1")
	private String adm1;

}
