package xyz.bromine0x23.shiningco.plugins.pixiv.representations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MetaPage {

	@JsonProperty("image_urls")
	private ImageUrls imageUrls = new ImageUrls();

}
