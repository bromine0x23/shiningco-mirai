package xyz.bromine0x23.shiningco.plugins.ff14;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class FF14Price {

	@JsonProperty("itemID")
	private String itemID;

	@JsonProperty("lastUploadTime")
	private Instant lastUploadTime;

	@JsonProperty("minPrice")
	private long minPrice;

	@JsonProperty("minPriceNQ")
	private long minPriceNQ;

	@JsonProperty("minPriceHQ")
	private long minPriceHQ;

	@JsonProperty("maxPrice")
	private long maxPrice;

	@JsonProperty("maxPriceNQ")
	private long maxPriceNQ;

	@JsonProperty("maxPriceHQ")
	private long maxPriceHQ;

}
