package xyz.bromine0x23.shiningco.representations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MessageRepresentation {

	@JsonProperty("target")
	String target;

	@JsonProperty("messages")
	List<Message> messages = new ArrayList<>();

	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
	@JsonSubTypes({
		@JsonSubTypes.Type(ParagraphMessage.class),
		@JsonSubTypes.Type(ImageMessage.class),
	})
	public interface Message {

		Type getType();

		enum Type {
			PARAGRAPH,
			IMAGE,
		}

	}

	@JsonTypeName("paragraph")
	@Data
	public static class ParagraphMessage implements Message {

		@JsonProperty("data")
		private Data data;

		@Override
		public Type getType() {
			return Type.PARAGRAPH;
		}

		@lombok.Data
		public static class Data {
			@JsonProperty("text")
			private String text;
		}
	}

	@JsonTypeName("image")
	@Data
	public static class ImageMessage implements Message {

		@JsonProperty("data")
		private Data data;

		@Override
		public Type getType() {
			return Type.IMAGE;
		}

		@lombok.Data
		public static class Data {
			@JsonProperty("file")
			private File file;


			@lombok.Data
			public static class File {
				@JsonProperty("url")
				private String url;
			}
		}

	}

}
