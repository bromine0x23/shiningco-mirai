package xyz.bromine0x23.shiningco.plugins.pixiv.representations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserPreview {

	@JsonProperty("user")
	private User user = new User();

	@JsonProperty("illusts")
	private List<Illustration> illustrations = new ArrayList<>();

	@JsonProperty("is_muted")
	private boolean isMuted;

}
