package xyz.bromine0x23.shiningco.plugins.pixiv.representations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MetaSinglePage {

	@JsonProperty("original_image_url")
	private String originalImageUrl;

}
