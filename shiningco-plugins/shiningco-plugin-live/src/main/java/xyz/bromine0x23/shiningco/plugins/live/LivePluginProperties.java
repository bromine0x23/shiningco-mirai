package xyz.bromine0x23.shiningco.plugins.live;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("shiningco.plugins.live")
@Data
public class LivePluginProperties {

	private List<Subscribe> subscribes = new ArrayList<>();

	@Data
	public static class Subscribe {
		private Matcher       matcher  = new Matcher();
		private List<Handler> handlers = new ArrayList<>();
	}

	@Data
	public static class Matcher {
		private BililiveRecorderEvent.EventType eventType;
		private Long                            roomId;
	}

	@Data
	public static class Handler {
		private Type   type   = Type.NOTICE;
		private Notice notice = new Notice();
		private Upload upload = new Upload();

		public enum Type {
			NOTICE,
			UPLOAD,
		}

		@Data
		public static class Notice {
			private String messagePattern = "[LIVE][${eventName}][${eventTimestamp}]: ${eventType} (${eventId})";
			private String receiver;
		}

		@Data
		public static class Upload {
			private String filenamePattern = "${eventRelativePath}";
			private String receiver;
		}

	}
}
