package xyz.bromine0x23.shiningco.plugins.eve;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
@Service
public class EVEService implements InitializingBean {

	private static final String DEFAULT_REGION = "10000002";
	private static final String DEFAULT_SYSTEM = "30000142";

	private final RateLimiter rateLimiter = RateLimiter.create(1);

	private final Map<String, EVEItem> items = new HashMap<>();

	private final RestTemplate restTemplate;

	public EVEService(
		RestTemplateBuilder restTemplateBuilder
	) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		var           mapper          = new YAMLMapper();
		var           typeTypeFactory = mapper.getTypeFactory();
		List<EVEItem> loadedItems;
		try (var inputStream = getClass().getResourceAsStream("items.yaml")) {
			loadedItems = mapper.readValue(inputStream, typeTypeFactory.constructCollectionType(List.class, EVEItem.class));
		}
		for (var loadedItem : loadedItems) {
			this.items.put(loadedItem.getName(), loadedItem);
		}
	}

	public EVEItem findItem(String itemName) {
		return items.get(itemName);
	}

	@Retryable
	public MarketPrices prices(String market, String region, String system, EVEItem item) {
		var api = "宁静".equals(market) ? "tqapi" : "api";
		if (region == null) {
			region = DEFAULT_REGION;
		}
		if (system == null) {
			system = DEFAULT_SYSTEM;
		}
		rateLimiter.acquire();
		return restTemplate.getForObject(
			"https://www.ceve-market.org/{api}/market/region/{region}/system/{system}/type/{item}.json",
			MarketPrices.class,
			api, region, system, item.getId()
		);
	}

	@Retryable
	public ByteArrayResource status() {
		rateLimiter.acquire();
		return restTemplate.getForObject("https://images.ceve-market.org/status/status.png", ByteArrayResource.class);
	}

}
