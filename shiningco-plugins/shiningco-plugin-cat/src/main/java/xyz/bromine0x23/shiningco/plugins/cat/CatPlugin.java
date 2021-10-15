package xyz.bromine0x23.shiningco.plugins.cat;

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

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Plugin(id = "cat", name = "猫插件", usage = """
	来点猫
	/cat
	""")
public class CatPlugin {

	private final CatService catService;

	private final LoadingCache<String, byte[]> imageCache;

	public CatPlugin(CatService catService) {
		this.catService = catService;
		this.imageCache = CacheBuilder.newBuilder()
			.expireAfterAccess(Duration.ofHours(1))
			.build(CacheLoader.from(catService::download));
	}

	@PluginCommand(pattern = "\\A(?:来点猫|[\\\\/]cat)\\Z", callRequired = false)
	public MessageChainBuilder get(@PluginCommandSubject Contact subject, MessageChainBuilder reply) {
		try {
			var meow = catService.get();
			if (meow != null) {
				try (var resource = ExternalResource.create(imageCache.get(meow.getFile()))) {
					reply.add(subject.uploadImage(resource));
				}
			}
		} catch (RestClientException | IOException exception) {
			reply.add("网络异常\uD83D\uDE3F");
		} catch (Exception exception) {
			log.debug("插件异常: ", exception);
			reply.add("插件异常\uD83D\uDE3F");
		}
		return reply;
	}

}
