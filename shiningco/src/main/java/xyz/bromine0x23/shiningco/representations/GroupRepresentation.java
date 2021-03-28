package xyz.bromine0x23.shiningco.representations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import net.mamoe.mirai.contact.Group;

@Value
@Builder
public class GroupRepresentation {

	@JsonProperty("id")
	long id;

	@JsonProperty("name")
	String name;

	@JsonProperty("avatarUrl")
	String avatarUrl;

	public static GroupRepresentation from(Group group) {
		return builder()
			.id(group.getId())
			.name(group.getName())
			.avatarUrl(group.getAvatarUrl())
			.build();
	}
}
