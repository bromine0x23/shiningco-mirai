package xyz.bromine0x23.shiningco.plugins.eve;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MarketPrice {

	@JsonProperty("max")
	private BigDecimal max;

	@JsonProperty("min")
	private BigDecimal min;

	@JsonProperty("volume")
	private long volume;

}
