package xyz.bromine0x23.shiningco.plugins.netease;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.MusicKind;
import net.mamoe.mirai.message.data.MusicShare;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.springframework.web.client.RestClientException;
import xyz.bromine0x23.shiningco.plugins.Plugin;
import xyz.bromine0x23.shiningco.plugins.PluginCommand;

import java.util.regex.Matcher;
import java.util.stream.Collectors;

@Slf4j
@Plugin(id = "netease", name = "网易云插件", usage = "来点<keyword>(歌|小曲|金曲)")
public class NeteasePlugin {

	private final NeteaseService neteaseService;

	private final RandomGenerator randomGenerator = new Well19937c(System.currentTimeMillis());

	public NeteasePlugin(
		NeteaseService neteaseService
	) {
		this.neteaseService = neteaseService;
	}

	@PluginCommand(pattern = "来点(?<keyword>.+?)(?<type>歌曲?|小曲|金曲)", callRequired = false)
	public MessageChainBuilder search(MessageEvent event, Matcher matcher, MessageChainBuilder reply) {
		try {
			var keyword = matcher.group("keyword");
			var type = matcher.group("type");
			var songs = neteaseService.search(keyword);
			if (songs.isEmpty()) {
				reply.add("没有找到" + keyword + type);
			} else{
				var song = songs.get(randomGenerator.nextInt(songs.size()));
				log.info("select: {}", song);

				var artists = song.getArtists().stream()
					.map(NeteaseService.Artist::getName)
					.collect(Collectors.joining("、"));

				reply.clear();
				var share = new MusicShare(
					MusicKind.NeteaseCloudMusic,
					song.getName(),
					"歌手：" + artists,
					"https://music.163.com/song?id=" + song.getId(),
					song.getAlbum().getPicUrl(),
					"https://music.163.com/song?id=" + song.getId()
				);
				reply.add(share);
			}
		} catch (RestClientException exception) {
			reply.add("网络异常");
		} catch (Exception exception) {
			log.debug("插件异常: ", exception);
			reply.add("网易云插件异常");
		}
		return reply;
	}

}
