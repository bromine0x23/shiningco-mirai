package xyz.bromine0x23.shiningco.plugins.pixiv.representations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ImageUrls {

	@JsonProperty("square_medium")
	private String squareMedium;

	@JsonProperty("medium")
	private String medium;

	@JsonProperty("large")
	private String large;

	@JsonProperty("original")
	private String original;

}
