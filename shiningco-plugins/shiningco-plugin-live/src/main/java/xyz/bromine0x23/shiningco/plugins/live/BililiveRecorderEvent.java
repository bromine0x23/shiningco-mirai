package xyz.bromine0x23.shiningco.plugins.live;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
public class BililiveRecorderEvent {

	@JsonProperty("EventType")
	private EventType type;

	@JsonProperty("EventTimestamp")
	private Instant timestamp;

	@JsonProperty("EventId")
	private String id;

	@JsonProperty("EventData")
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "EventType")
	private EventData data;

	public enum EventType {
		SessionStarted,
		SessionEnded,
		FileOpening,
		FileClosed
	}

	@Data
	@JsonSubTypes({
		@JsonSubTypes.Type(value = SessionStartedEventData.class, name = "SessionStarted"),
		@JsonSubTypes.Type(value = FileOpeningEventData.class, name = "FileOpening"),
		@JsonSubTypes.Type(value = FileClosedEventData.class, name = "FileClosed"),
		@JsonSubTypes.Type(value = SessionEndedEventData.class, name = "SessionEnded"),
	})
	public static class EventData {

		@JsonProperty("SessionId")
		private String sessionId;

		@JsonProperty("RoomId")
		private Long roomId;

		@JsonProperty("ShortId")
		private Long shortId;

		@JsonProperty("Name")
		private String name;

		@JsonProperty("Title")
		private String title;

		@JsonProperty("AreaNameParent")
		private String areaNameParent;

		@JsonProperty("AreaNameChild")
		private String areaNameChild;

	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class SessionStartedEventData extends EventData {
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class SessionEndedEventData extends EventData {
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class FileOpeningEventData extends EventData {

		@JsonProperty("RelativePath")
		private String relativePath;

		@JsonProperty("FileOpenTime")
		private Instant fileOpenTime;

	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class FileClosedEventData extends EventData {

		@JsonProperty("RelativePath")
		private String relativePath;

		@JsonProperty("FileSize")
		private Integer fileSize;

		@JsonProperty("Duration")
		private Double duration;

		@JsonProperty("FileOpenTime")
		private Instant fileOpenTime;

		@JsonProperty("FileCloseTime")
		private Instant fileCloseTime;

	}

}
