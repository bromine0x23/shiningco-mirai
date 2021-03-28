package xyz.bromine0x23.shiningco.plugins.eve;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MarketPrices {

	@JsonProperty("all")
	private MarketPrice all;

	@JsonProperty("buy")
	private MarketPrice buy;

	@JsonProperty("sell")
	private MarketPrice sell;

}
