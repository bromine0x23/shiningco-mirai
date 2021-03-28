package xyz.bromine0x23.shiningco.compiler.representations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BuildResult {

	@JsonProperty("code")
	private int code = 0;

	@JsonProperty("stdout")
	private List<Line> stdout = new ArrayList<>();

	@JsonProperty("stderr")
	private List<Line> stderr = new ArrayList<>();

	@JsonProperty("execResult")
	private ExecResult execResult = new ExecResult();

}
