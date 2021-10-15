package xyz.bromine0x23.shiningco.plugins.pixiv;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.ForwardMessageBuilder;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;
import org.apache.commons.math3.distribution.ZipfDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well512a;
import org.jsoup.Jsoup;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.client.RestClientException;
import xyz.bromine0x23.shiningco.plugins.Plugin;
import xyz.bromine0x23.shiningco.plugins.PluginCommand;
import xyz.bromine0x23.shiningco.plugins.pixiv.representations.Illustration;
import xyz.bromine0x23.shiningco.plugins.pixiv.representations.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

@Slf4j
@Plugin(id = "pixiv", name = "Pixiv插件", usage = "来点<关键字>图\n来点<画师>画的图")
@EnableConfigurationProperties(PixivPluginProperties.class)
public class PixivPlugin {

	private final RandomGenerator randomGenerator = new Well512a(System.currentTimeMillis());

	private final PixivService pixivService;

	public PixivPlugin(PixivService pixivService) {
		this.pixivService = pixivService;
	}

	@PluginCommand(pattern = "\\A[\\\\/]pixiv(?:\\.keyword)?\\s+(?<keyword>..+)", callRequired = false)
	@PluginCommand(pattern = "来点(?<keyword>.+?)(?<!画的)图", callRequired = false)
	public MessageChainBuilder searchByKeyword(MessageEvent event, Matcher matcher, MessageChainBuilder reply) {
		try {
			var keyword       = matcher.group("keyword");
			var illustrations = pixivService.searchRandomOneByKeyword(keyword, false);
			if (!illustrations.isEmpty()) {
				replyForFound(event, illustrations, reply);
			} else {
				reply.add("没有找到" + keyword + "图");
			}
		} catch (RestClientException | IOException exception) {
			reply.add("网络异常");
		} catch (Exception exception) {
			log.debug("插件异常: ", exception);
			reply.add("Pixiv插件异常");
		}
		return reply;
	}

	@PluginCommand(pattern = "\\A[\\\\/]pixiv\\.artist\\s+(?<artist>.+)", callRequired = false)
	@PluginCommand(pattern = "来点(?<artist>.+?)画的图", callRequired = false)
	public MessageChainBuilder searchByArtist(MessageEvent event, Matcher matcher, MessageChainBuilder reply) {
		try {
			var artist        = matcher.group("artist");
			var illustrations = pixivService.searchRandomOneByArtist(artist, false);
			if (!illustrations.isEmpty()) {
				replyForFound(event, illustrations, reply);
			} else {
				reply.add("没有找到" + artist + "画的图");
			}
		} catch (RestClientException | IOException exception) {
			reply.add("网络异常");
		} catch (Exception exception) {
			log.debug("插件异常: ", exception);
			reply.add("Pixiv插件异常");
		}
		return reply;
	}

	private void replyForFound(MessageEvent event, List<Illustration> illustrations, MessageChainBuilder reply) throws IOException {
		var illustration = randomOne(illustrations);

		var imageUrls = new ArrayList<String>();
		imageUrls.add(illustration.getMetaSinglePage().getOriginalImageUrl());
		for (var metaPage : illustration.getMetaPages()) {
			imageUrls.add(metaPage.getImageUrls().getOriginal());
		}

		var images = imageUrls.stream().filter(Objects::nonNull).limit(10).map(pixivService::download).toList();

		var forwardMessageBuilder = new ForwardMessageBuilder(event.getSender());

		forwardMessageBuilder.add(event.getSender(), event.getMessage(), event.getTime());
		forwardMessageBuilder.add(event.getBot(), new PlainText("标题：" + illustration.getTitle()));
		forwardMessageBuilder.add(event.getBot(), new PlainText("地址：https://www.pixiv.net/artworks/" + illustration.getId()));
		forwardMessageBuilder.add(event.getBot(), new PlainText("作者：" + illustration.getUser().getName()));
		forwardMessageBuilder.add(event.getBot(), new PlainText("说明：" + truncate(illustration.getCaption())));
		forwardMessageBuilder.add(event.getBot(), new PlainText("标签：" + illustration.getTags().stream().map(Tag::getBestName).limit(20).collect(Collectors.joining(" "))));
		forwardMessageBuilder.add(event.getBot(), new PlainText("上传时间：" + illustration.getCreateDate().toLocalDateTime()));
		for (var image : images) {
			try (var resource = ExternalResource.create(image)) {
				forwardMessageBuilder.add(event.getBot(), event.getSubject().uploadImage(resource));
				// reply.add(event.getSubject().uploadImage(resource));
			}
		}

		reply.add(forwardMessageBuilder.build());
	}

	private Illustration randomOne(List<Illustration> illustrations) {
		var distribution = new ZipfDistribution(randomGenerator, illustrations.size(), 0.5);
		// var distribution = new UniformIntegerDistribution(1, illustrations.size());
		return illustrations.get(distribution.sample() - 1);
	}

	private String truncate(String text) {
		text = Jsoup.parseBodyFragment(text).text();
		if (text.length() > 500) {
			text = text.substring(0, 490) + " [截断]";
		}
		return text;
	}

}
