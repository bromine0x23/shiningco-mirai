package xyz.bromine0x23.shiningco.plugins.bangumi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BangumiSearchResult {

	@JsonProperty("results")
	private int count;

	@JsonProperty("list")
	private List<BangumiSubject> subjects;

}
