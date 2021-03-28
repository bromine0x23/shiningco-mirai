package xyz.bromine0x23.shiningco.plugins.eve;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EVEItem {
	@JsonProperty("id")
	private long id;

	@JsonProperty("name")
	private String name;
}
