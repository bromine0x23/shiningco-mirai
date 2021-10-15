package xyz.bromine0x23.shiningco.plugins.bangumi;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BangumiSubjectType {
	BOOK(1),
	ANIME(2),
	MUSIC(3),
	GAME(4),
	REAL(5),
	;

	private final int code;

	@JsonValue
	public int getCode() {
		return code;
	}

	@Override
	public String toString() {
		return String.valueOf(getCode());
	}

}
