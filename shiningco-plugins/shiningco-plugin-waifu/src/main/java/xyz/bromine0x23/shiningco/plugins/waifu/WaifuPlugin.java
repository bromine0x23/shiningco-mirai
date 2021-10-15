package xyz.bromine0x23.shiningco.plugins.waifu;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
import org.springframework.web.client.RestClientException;
import xyz.bromine0x23.shiningco.plugins.Plugin;
import xyz.bromine0x23.shiningco.plugins.PluginCommand;
import xyz.bromine0x23.shiningco.plugins.PluginCommandSubject;
import xyz.bromine0x23.shiningco.runtime.WorkTimeService;

import java.io.IOException;
import java.time.Duration;
import java.util.regex.Matcher;

@Slf4j
@Plugin(id = "waifu", name = "老婆插件", usage = """
	来点老婆
	/waifu
	/waifu <category> # SFW
	/waifu! <category>  # NSFW
	# SFW categories: waifu, neko, shinobu, megumin, bully, cuddle, cry, hug, awoo, kiss, lick, pat, smug, bonk, yeet, blush, smile, wave, highfive, handhold, nom, bite, glomp, slap, kill, kick, happy, wink, poke, dance, cringe
	# NSFW categories: waifu, neko, trap, blowjob
	""")
public class WaifuPlugin {

	private final WaifuService waifuService;

	private final WorkTimeService workTimeService;

	private final LoadingCache<String, byte[]> imageCache;

	public WaifuPlugin(
		WaifuService waifuService,
		WorkTimeService workTimeService
	) {
		this.waifuService    = waifuService;
		this.imageCache      = CacheBuilder.newBuilder()
			.expireAfterAccess(Duration.ofHours(1))
			.build(CacheLoader.from(waifuService::download));
		this.workTimeService = workTimeService;
	}

	@PluginCommand(pattern = "\\A(?:来点老婆|[\\\\/]waifu(?<unsafe>!)?(?: (?<category>\\S+))?)\\Z", callRequired = false)
	public MessageChainBuilder get(@PluginCommandSubject Contact subject, Matcher matcher, MessageChainBuilder reply) {
		try {
			var safeForWork = workTimeService.duringWorkTime() || matcher.group("unsafe") == null;
			var waifu       = waifuService.get(safeForWork, matcher.group("category"));
			if (waifu != null && waifu.getUrl() != null) {
				try (var resource = ExternalResource.create(imageCache.get(waifu.getUrl()))) {
					reply.add(subject.uploadImage(resource));
				}
			} else {
				reply.add("Not found");
			}
		} catch (RestClientException | IOException exception) {
			reply.add("Waifu插件网络异常");
		} catch (Exception exception) {
			log.debug("插件异常: ", exception);
			reply.add("Waifu插件异常");
		}
		return reply;
	}

}
