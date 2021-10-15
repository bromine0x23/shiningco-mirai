package xyz.bromine0x23.shiningco.plugins.jav;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.ForwardMessageBuilder;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;
import org.jsoup.nodes.Document;
import org.springframework.web.client.RestClientException;
import xyz.bromine0x23.shiningco.plugins.Plugin;
import xyz.bromine0x23.shiningco.plugins.PluginCommand;
import xyz.bromine0x23.shiningco.runtime.WorkTimeService;

import java.io.IOException;
import java.time.Duration;
import java.util.regex.Matcher;

@Slf4j
@Plugin(id = "jav", name = "验车插件", usage = "验车<番号>")
public class JavPlugin {

	private final JavbusService javbusService;

	private final WorkTimeService workTimeService;

	private final LoadingCache<String, byte[]> coverCache;

	public JavPlugin(
		JavbusService javbusService,
		WorkTimeService workTimeService
	) {
		this.javbusService   = javbusService;
		this.workTimeService = workTimeService;
		this.coverCache      = CacheBuilder.newBuilder()
			.expireAfterAccess(Duration.ofMinutes(5))
			.build(CacheLoader.from(javbusService::download));
	}

	@PluginCommand(pattern = "\\A[\\\\/]验车\\s*(?<code>.+)\\s*\\Z", callRequired = false)
	public MessageChainBuilder randomProjectByKeyword(MessageEvent event, Matcher matcher, MessageChainBuilder reply) {
		try {
			var code = matcher.group("code");

			var document = javbusService.search(code);
			if (document != null) {
				var jav   = parse(document);
				var cover = coverCache.get(jav.getCoverUrl());

				var forwardMessageBuilder = new ForwardMessageBuilder(event.getSender());
				forwardMessageBuilder.add(event.getSender(), event.getMessage(), event.getTime());
				forwardMessageBuilder.add(event.getSender(), new PlainText(jav.getTitle()));
				forwardMessageBuilder.add(event.getSender(), new PlainText("制作商: " + jav.getManufacturer()));
				forwardMessageBuilder.add(event.getSender(), new PlainText("类别: " + String.join(" ", jav.getTags())));
				forwardMessageBuilder.add(event.getSender(), new PlainText("出演: " + String.join(" ", jav.getActors())));
				try (var resource = ExternalResource.create(cover)) {
					forwardMessageBuilder.add(event.getSender(), event.getSubject().uploadImage(resource));
				}
				reply.add(forwardMessageBuilder.build());
//				reply.add("磁链：" + jav.getMagnets().stream().limit(3).collect(Collectors.joining("\n")));
			} else {
				reply.add("没有找到型号为" + code + "的车");
			}
		} catch (RestClientException | IOException exception) {
			reply.add("网络异常");
		} catch (Exception exception) {
			log.debug("插件异常: ", exception);
			reply.add("Jav插件异常");
		}
		return reply;
	}

	private Jav parse(Document document) {
		var jav = new Jav();
		{
			var element = document.select("body > .container > h3").first();
			jav.setTitle(element.text());
		}
		{
			var element = document.select("body > .container > .movie > .info > p > span:contains(製作商) + a").first();
			jav.setManufacturer(element.text());
		}
		{
			var elements = document.select("body > .container > .movie > .info > p:contains(類別) + p > span > label > a");
			jav.setTags(elements.eachText());
		}
		{
			var elements = document.select("body > .container > .movie > .info > p:has(span:contains(演員)) + ul + p > span > a");
			jav.setActors(elements.eachText());
		}
		{
			var element = document.select("body > .container > .movie > .screencap > a > img").first();
			jav.setCoverUrl(element.attr("abs:src"));
		}
//		{
//			var elements = document.select("#magnet-table > tr:first-child > a");
//			jav.setMagnets(elements.eachAttr("href"));
//		}
		return jav;

	}

}
