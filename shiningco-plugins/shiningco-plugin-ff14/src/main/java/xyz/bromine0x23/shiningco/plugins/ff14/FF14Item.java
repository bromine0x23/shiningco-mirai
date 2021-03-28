package xyz.bromine0x23.shiningco.plugins.ff14;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FF14Item {
	@JsonProperty("id")
	private String id;

	@JsonProperty("name")
	private String name;

}
