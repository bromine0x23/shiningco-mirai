package xyz.bromine0x23.shiningco.plugins.pixiv.representations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class User {

	@JsonProperty("id")
	private long id;

	@JsonProperty("name")
	private String name;

}
