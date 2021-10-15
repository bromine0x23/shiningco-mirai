package xyz.bromine0x23.shiningco.plugins.weather;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import xyz.bromine0x23.shiningco.plugins.Plugin;
import xyz.bromine0x23.shiningco.plugins.PluginCommand;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;

@Slf4j
@Plugin(id = "weather", name = "天气插件", usage = "\\天气 <城市>")
@EnableConfigurationProperties(WeatherPluginProperties.class)
public class WeatherPlugin {

	private final WeatherPluginProperties properties;

	private final QWeatherApiService qweatherApiService;

	private final LoadingCache<String, List<QWeatherLocation>> locationsCache = CacheBuilder.newBuilder()
		.expireAfterWrite(Duration.ofSeconds(3600L))
		.build(CacheLoader.from(this::queryLocations));

	public WeatherPlugin(
		WeatherPluginProperties properties,
		QWeatherApiService qweatherApiService
	) {
		this.properties         = properties;
		this.qweatherApiService = qweatherApiService;
	}

	@PluginCommand(pattern = "[\\\\/]天气\\s*(?<location>.+)", callRequired = false)
	public MessageChainBuilder weather(MessageEvent event, Matcher matcher, MessageChainBuilder reply) throws ExecutionException {
		var locationKey = matcher.group("location");
		var location = queryLocation(locationKey);
		if (location == null) {
			reply.add("没有找到区域：" + locationKey);
		} else {
			var now = qweatherApiService.now(location.getId(), properties.getQweatherKey());
			var dailies = qweatherApiService.latest3d(location.getId(), properties.getQweatherKey());
			reply.add(format(location)  + "天气：");
			reply.add("\n");
			reply.add(String.format(
				"当前：%s ℃, %s (%s)",
				now.getTemperature(),
				now.getText(),
				now.getObsTime()
			));
			for (var daily : dailies) {
				reply.add("\n");
				reply.add(String.format(
					"%s：%s ~ %s ℃, %s",
					daily.getFxDate(),
					daily.getTemperatureMin(),
					daily.getTemperatureMax(),
					Objects.equals(daily.getTextDay(), daily.getTextNight()) ? daily.getTextDay() : daily.getTextDay() + "转" + daily.getTextNight()
				));
			}
		}
		return reply;
	}

	private QWeatherLocation queryLocation(String location) throws ExecutionException {
		var locations = locationsCache.get(location);
		return locations.isEmpty() ? null : locations.get(0);
	}

	private List<QWeatherLocation> queryLocations(String location) {
		return qweatherApiService.locations(location, properties.getQweatherKey());
	}

	private String format(QWeatherLocation location) {
		if (Objects.equals(location.getName(), location.getAdm2())) {
			return location.getName();
		} else {
			return location.getAdm2() + location.getName();
		}
	}
}
