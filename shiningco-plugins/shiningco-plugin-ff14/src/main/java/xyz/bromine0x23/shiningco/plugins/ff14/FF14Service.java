package xyz.bromine0x23.shiningco.plugins.ff14;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class FF14Service implements InitializingBean {

	private static final String API_URL = "https://universalis.app/api/{market}/{item}";

	private final Map<String, FF14Item> items = new HashMap<>();

	private final Set<String> markets = new HashSet<>();

	private final RestTemplate restTemplate;

	public FF14Service(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		var                   mapper          = new YAMLMapper();
		var                   typeTypeFactory = mapper.getTypeFactory();
		try (var inputStream = getClass().getResourceAsStream("items.yaml")) {
			List<FF14Item> loadedItems = mapper.readValue(inputStream, typeTypeFactory.constructCollectionType(List.class, FF14Item.class));
			for (var loadedItem : loadedItems) {
				this.items.put(loadedItem.getName(), loadedItem);
			}
		}
		try (var inputStream = getClass().getResourceAsStream("markets.yaml")) {
			List<String> marketList = mapper.readValue(inputStream, typeTypeFactory.constructCollectionType(List.class, String.class));
			this.markets.addAll(marketList);
		}
	}

	public boolean hasMarket(String market) {
		return markets.contains(market);
	}

	public FF14Item findItem(String itemName) {
		return items.get(itemName);
	}

	public Optional<FF14Price> price(String market, FF14Item item) {
		try {
			var uri = UriComponentsBuilder.fromHttpUrl(API_URL).build(market, item.getId());
			return Optional.ofNullable(restTemplate.getForObject(uri, FF14Price.class));
		} catch (Exception exception) {
			log.debug("Exception: ", exception);
		}
		return Optional.empty();

	}

}
