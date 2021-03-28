package xyz.bromine0x23.shiningco.plugins.javbus;

import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;
import xyz.bromine0x23.shiningco.plugins.PluginCommand;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;

@Slf4j
@Component
public class JavbusPlugin {

	private final WebClient webClient;

	public JavbusPlugin() {
		var httpClient = HttpClient.create().proxy(proxy -> proxy.type(ProxyProvider.Proxy.SOCKS5).host("127.0.0.1").port(1080));
		var connector  = new ReactorClientHttpConnector(httpClient);
		this.webClient = WebClient.builder()
			.clientConnector(connector)
			.build();
	}

	@PluginCommand("\\A验车\\s?([\\w-]+)\\Z")
	public void info(MessageEvent event, Matcher matcher) throws IOException {
		var code = matcher.group(1);
		var film = find(code);
		log.debug("film: {}", film);
		var reply = new MessageChainBuilder();
		if (film != null) {
			reply.add(film.getTitle());
			if (film.getCover() != null) {
				var image = event.getSubject().uploadImage(ExternalResource.create(film.getCover()));
				reply.add(image);
			}
		} else {
			reply.add("未找到影片：" + code);
		}
		event.getSubject().sendMessage(reply.build());
	}

	private Film find(String code) {
		try {
			var url      = UriComponentsBuilder.fromHttpUrl("https://www.javbus.com/{code}").build(code).toString();
			var document = Jsoup.connect(url).get();
			return Film.builder()
				.title(extractTitle(document))
//				.actors(extractActors(document))
				.cover(fetchCover(document))
				.build();
		} catch (Exception exception) {
			log.debug("Exception: ", exception);
			return null;
		}
	}

	private String extractTitle(Document document) {
		var element = document.select("body > .container > h3").first();
		return element.text();
	}

	private List<String> extractActors(Document document) {
		var elements = document.select("body > .container > .movie > .info > p:nth-child(11) > span > a");
		return elements.eachText();
	}

	private InputStream fetchCover(Document document) {
		try {
			var coverElement = document.select("body > .container > .movie > .screencap > a > img").first();
			var coverUrl     = coverElement.attr("src");
			log.debug("fetch cover: {}", coverUrl);
			return webClient.get().uri(coverUrl).retrieve().bodyToMono(byte[].class).map(ByteArrayInputStream::new).block();
		} catch (Exception exception) {
			log.debug("Exception: ", exception);
			return null;
		}
	}

	@Value
	@Builder
	public static class Film {

		String title;

		List<String> actors;

		InputStream cover;

	}
}
