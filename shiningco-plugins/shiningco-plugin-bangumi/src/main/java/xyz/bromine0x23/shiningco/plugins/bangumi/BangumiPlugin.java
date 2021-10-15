package xyz.bromine0x23.shiningco.plugins.bangumi;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
import org.apache.commons.math3.distribution.ZipfDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well512a;
import xyz.bromine0x23.shiningco.plugins.Plugin;
import xyz.bromine0x23.shiningco.plugins.PluginCommand;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;

@Slf4j
@Plugin(id = "bangumi", name = "番组插件", usage = """
	来点<keyword>番
	""")
public class BangumiPlugin {

	private final RandomGenerator randomGenerator = new Well512a(System.currentTimeMillis());

	private final BangumiService bangumiService;

	private final LoadingCache<Integer, BangumiSubject> subjectCache;

	private final LoadingCache<String, byte[]> coverCache;

	public BangumiPlugin(
		BangumiService bangumiService
	) {
		this.bangumiService = bangumiService;
		this.subjectCache   = CacheBuilder.newBuilder()
			.expireAfterAccess(Duration.ofHours(1))
			.build(CacheLoader.from(bangumiService::show));
		this.coverCache     = CacheBuilder.newBuilder()
			.expireAfterAccess(Duration.ofHours(1))
			.build(CacheLoader.from(bangumiService::download));
	}

	@PluginCommand(pattern = "来点\\s*(?<keyword>.+)\\s*番", callRequired = false)
	public MessageChainBuilder randomSubjectByKeyword(MessageEvent event, Matcher matcher, MessageChainBuilder reply) {
		try {
			var keyword  = matcher.group("keyword");
			var subjects = bangumiService.search(keyword, BangumiSubjectType.ANIME);
			if (subjects != null && !subjects.isEmpty()) {
				var subject = select(subjects);
				subject = subjectCache.get(subject.getId());
				reply.add(String.format("%s ／ %s", subject.getName(), subject.getNameCN()));
				reply.add("\n");
				reply.add(subject.getUrl());
				reply.add("\n");
				reply.add("放送开始：" + subject.getAirDate());
				reply.add("\n");

				var score = subject.getRating().getScore();
				if (score != null) {
					reply.add(String.format("评分：%.1f（%d 人评分）", score, subject.getRating().getTotal()));
					reply.add("\n");
				}

				reply.add(subject.getSummary());

				var coverUrl = subject.getImages().getLarge();
				if (coverUrl != null) {
					try (var resource = ExternalResource.create(coverCache.get(coverUrl))) {
						reply.add(event.getSubject().uploadImage(resource));
					}
				}
			} else {
				reply.add(String.format("没有找到有关【%s】的番组", keyword));
			}
		} catch (Exception exception) {
			log.debug("插件异常: ", exception);
			reply.add("番组插件异常");
		}
		return reply;
	}

	private BangumiSubject select(List<BangumiSubject> subjects) {
		var distribution = new ZipfDistribution(randomGenerator, subjects.size(), 0.75);
		// var distribution = new UniformIntegerDistribution(1, subjects.size());
		return subjects.get(distribution.sample() - 1);
	}

}
