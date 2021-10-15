package xyz.bromine0x23.shiningco.plugins.live;


import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.FileSupported;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.RemoteFile;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.TaskExecutor;
import xyz.bromine0x23.shiningco.plugins.Plugin;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Slf4j
@Plugin(id = "live", name = "直播插件", usage = "<无命令式功能>")
@EnableConfigurationProperties(LivePluginProperties.class)
public class LivePlugin {

	private final LivePluginProperties properties;

	private final Bot bot;

	private final TaskExecutor taskExecutor;

	private final Map<Predicate<BililiveRecorderEvent>, List<Consumer<BililiveRecorderEvent>>> subscribes = new LinkedHashMap<>();

	public LivePlugin(
		LivePluginProperties properties,
		@Lazy Bot bot,
		TaskExecutor taskExecutor
	) {
		this.properties   = properties;
		this.bot          = bot;
		this.taskExecutor = taskExecutor;
	}

	@PostConstruct
	public void postConstruct() {
		for (var subscribe : properties.getSubscribes()) {
			var matcherProperties = subscribe.getMatcher();
			var matchers          = new ArrayList<Predicate<BililiveRecorderEvent>>();
			if (matcherProperties.getEventType() != null) {
				matchers.add(event -> Objects.equals(matcherProperties.getEventType(), event.getType()));
			}
			if (matcherProperties.getRoomId() != null) {
				matchers.add(event -> Objects.equals(matcherProperties.getRoomId(), event.getData().getRoomId()));
			}
			var matcher = matchers.stream().reduce(Predicate::and).orElse(e -> false);

			var handlers = subscribe.getHandlers().stream()
				.map(handlerProperties -> switch (handlerProperties.getType()) {
					case NOTICE -> new NoticeHandler(handlerProperties.getNotice());
					case UPLOAD -> new UploadHandler(handlerProperties.getUpload());
				})
				.toList();

			subscribes.put(matcher, handlers);
		}
	}

	public static Map<String, Object> toValues(BililiveRecorderEvent event) {
		var eventData = event.getData();
		var values    = new HashMap<String, Object>();
		values.put("eventType", event.getType());
		values.put("eventId", event.getId());
		values.put("eventTimestamp", Objects.toString(event.getTimestamp()));
		values.put("eventName", eventData.getName());
		values.put("eventTitle", eventData.getTitle());
		values.put("eventRoomId", Objects.toString(eventData.getRoomId()));
		values.put("eventAreaNameParent", eventData.getAreaNameParent());
		values.put("eventAreaNameChild", eventData.getAreaNameChild());

		switch (event.getType()) {
			case FileOpening -> {
				var realEventData = (BililiveRecorderEvent.FileOpeningEventData) eventData;
				values.put("recordRelativePath", realEventData.getRelativePath());
				values.put("recordFileOpenTime", realEventData.getFileOpenTime());
			}
			case FileClosed -> {
				var realEventData = (BililiveRecorderEvent.FileClosedEventData) eventData;
				values.put("recordRelativePath", realEventData.getRelativePath());
				values.put("recordFileSize", realEventData.getFileSize());
				values.put("recordDuration", realEventData.getDuration());
				values.put("recordFileOpenTime", realEventData.getFileOpenTime());
				values.put("recordFileCloseTime", realEventData.getFileCloseTime());
			}
			default -> {
				// ignored
			}
		}

		return values;
	}

	private Optional<Contact> parseReceiver(String receiver) {
		if (receiver.startsWith("friend:")) {
			var friendId = Long.parseLong(receiver.substring(7));
			return Optional.ofNullable(bot.getFriend(friendId));
		} else if (receiver.startsWith("group:")) {
			var groupId = Long.parseLong(receiver.substring(6));
			return Optional.ofNullable(bot.getGroup(groupId));
		} else {
			log.warn("Invalid event receiver: {}, ignored.", receiver);
			return Optional.empty();
		}
	}

	public class NoticeHandler implements Consumer<BililiveRecorderEvent> {

		private final LivePluginProperties.Handler.Notice properties;

		public NoticeHandler(LivePluginProperties.Handler.Notice properties) {
			this.properties = properties;
		}

		@Override
		public void accept(BililiveRecorderEvent event) {
			var receiver = properties.getReceiver();
			var message  = StringSubstitutor.replace(properties.getMessagePattern(), toValues(event));
			parseReceiver(receiver).ifPresent(contact -> contact.sendMessage(new PlainText(message)));
		}
	}

	public class UploadHandler implements Consumer<BililiveRecorderEvent> {

		private final LivePluginProperties.Handler.Upload properties;

		public UploadHandler(LivePluginProperties.Handler.Upload properties) {
			this.properties = properties;
		}

		@Override
		public void accept(BililiveRecorderEvent event) {
			var receiver = properties.getReceiver();
			var filename = StringSubstitutor.replace(properties.getFilenamePattern(), toValues(event));
			parseReceiver(receiver).ifPresent(contact -> {
				if (contact instanceof FileSupported fileSupported) {
					var file        = new File(filename);
					var fileMessage = RemoteFile.uploadFile(fileSupported, file.getName(), file);
					contact.sendMessage(fileMessage);
				}
			});
		}
	}

	public void handle(BililiveRecorderEvent event) {
		for (var subscribe : subscribes.entrySet()) {
			try {
				var matcher = subscribe.getKey();
				if (matcher.test(event)) {
					var handlers = subscribe.getValue();
					for (var handler : handlers) {
						taskExecutor.execute(() -> handler.accept(event));
					}
				}
			} catch (Exception exception) {
				log.warn("Exception: ", exception);
			}
		}
	}

}
