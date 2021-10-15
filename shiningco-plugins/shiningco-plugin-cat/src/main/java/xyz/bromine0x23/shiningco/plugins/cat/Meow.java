package xyz.bromine0x23.shiningco.plugins.cat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Meow {

	@JsonProperty("file")
	private String file;

}
