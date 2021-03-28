package xyz.bromine0x23.shiningco.plugins.pixiv.representations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchIllustResult {

	@JsonProperty("illusts")
	private List<Illustration> illustrations = new ArrayList<>();

	@JsonProperty("next_url")
	private String nextUrl;

	@JsonProperty("search_span_limit")
	private long searchSpanLimit;

}
