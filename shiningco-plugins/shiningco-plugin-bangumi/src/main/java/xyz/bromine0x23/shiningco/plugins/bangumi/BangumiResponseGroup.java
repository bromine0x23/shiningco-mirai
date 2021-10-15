package xyz.bromine0x23.shiningco.plugins.bangumi;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BangumiResponseGroup {
	SMALL("small"),
	MEDIUM("medium"),
	LARGE("large"),
	;

	private final String code;

	@JsonValue
	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return getCode();
	}
}
