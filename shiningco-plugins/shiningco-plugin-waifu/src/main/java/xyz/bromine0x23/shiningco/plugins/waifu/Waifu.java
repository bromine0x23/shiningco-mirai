package xyz.bromine0x23.shiningco.plugins.waifu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Waifu {

	@JsonProperty("message")
	private String message;

	@JsonProperty("url")
	private String url;

}
