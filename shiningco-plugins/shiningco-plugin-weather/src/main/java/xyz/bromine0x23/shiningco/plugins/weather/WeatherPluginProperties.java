package xyz.bromine0x23.shiningco.plugins.weather;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("shiningco.plugins.weather")
@Data
public class WeatherPluginProperties {

	private String qweatherKey;

}
