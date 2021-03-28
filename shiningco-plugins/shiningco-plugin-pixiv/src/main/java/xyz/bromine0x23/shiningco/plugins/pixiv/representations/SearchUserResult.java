package xyz.bromine0x23.shiningco.plugins.pixiv.representations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SearchUserResult {

	@JsonProperty("user_previews")
	private List<UserPreview> userPreviews;

	@JsonProperty("next_url")
	private String nextUrl;

}
