package xyz.bromine0x23.shiningco.plugins.pixiv.representations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Tag {

	@JsonProperty("name")
	private String name;

	@JsonProperty("translated_name")
	private String translatedName;

	public String getBestName() {
		return translatedName != null ? translatedName : name;
	}

}
