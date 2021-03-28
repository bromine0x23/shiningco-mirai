package xyz.bromine0x23.shiningco.representations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import net.mamoe.mirai.contact.Friend;

@Value
@Builder
public class FriendRepresentation {

	@JsonProperty("id")
	long id;

	@JsonProperty("nick")
	String nick;

	@JsonProperty("remark")
	String remark;

	@JsonProperty("avatarUrl")
	String avatarUrl;

	public static FriendRepresentation from(Friend friend) {
		return builder()
			.id(friend.getId())
			.nick(friend.getNick())
			.remark(friend.getRemark())
			.avatarUrl(friend.getAvatarUrl())
			.build();
	}
}
