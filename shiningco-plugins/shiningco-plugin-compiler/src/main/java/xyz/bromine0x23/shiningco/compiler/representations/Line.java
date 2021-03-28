package xyz.bromine0x23.shiningco.compiler.representations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Line {

	@JsonProperty("text")
	private String text;

}
