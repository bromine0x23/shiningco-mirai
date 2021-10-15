package xyz.bromine0x23.shiningco.plugins.github;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.distribution.ZipfDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.github.GHRepository;
import xyz.bromine0x23.shiningco.plugins.Plugin;
import xyz.bromine0x23.shiningco.plugins.PluginCommand;

import java.util.List;
import java.util.regex.Matcher;

@Slf4j
@Plugin(id = "github", name = "GitHub插件", usage = "来点<关键字>项目")
public class GithubPlugin {

	private final GithubService githubService;

	private final RandomGenerator randomGenerator = new Well19937c(System.currentTimeMillis());

	public GithubPlugin(GithubService githubService) {
		this.githubService = githubService;
	}

	@PluginCommand(pattern = "\\A[\\\\/]github\\s+(?<keyword>..+)", callRequired = false)
	@PluginCommand(pattern = "来点(?<keyword>.+)(?<!写的|喜欢的)项目", callRequired = false)
	public MessageChainBuilder randomProjectByKeyword(MessageEvent event, Matcher matcher, MessageChainBuilder reply) {
		try {
			var keyword = matcher.group("keyword");

			var repositories = githubService.searchRepositories(keyword);
			var repository   = randomOne(repositories);

			if (repository != null) {
				reply.add(repository.getFullName() + " ☆" + repository.getStargazersCount());
				reply.add("\n");
				reply.add(repository.getHtmlUrl().toString());
				reply.add("\n");
				reply.add(truncate(repository.getDescription()));
			} else {
				reply.add("没有找到" + keyword + "项目");
			}
		} catch (Exception exception) {
			log.debug("插件异常: ", exception);
			reply.add("GitHub插件异常");
		}

		return reply;
	}

	@PluginCommand(pattern = "来点(?<user>.+)写的项目", callRequired = false)
	public MessageChainBuilder randomProjectFromUser(MessageEvent event, Matcher matcher, MessageChainBuilder reply) {
		try {
			var user = matcher.group("user");

			var repositories = githubService.searchRepositoriesByUser(user);
			var repository   = randomOne(repositories);

			if (repository != null) {
				reply.add(repository.getFullName() + " ☆" + repository.getStargazersCount());
				reply.add("\n");
				reply.add(repository.getHtmlUrl().toString());
				reply.add("\n");
				reply.add(truncate(repository.getDescription()));
			} else {
				reply.add("没有找到" + user + "写的项目");
			}
		} catch (Exception exception) {
			log.debug("插件异常: ", exception);
			reply.add("GitHub插件异常");
		}
		return reply;
	}

	@PluginCommand(pattern = "来点(?<user>.+)喜欢的项目", callRequired = false)
	public MessageChainBuilder randomProjectFromUserStarred(MessageEvent event, Matcher matcher, MessageChainBuilder reply) {
		try {
			var user         = matcher.group("user");
			var repositories = githubService.searchRepositoriesByUserStarred(user);
			var repository   = randomOne(repositories);
			if (repository != null) {
				reply.add(repository.getFullName() + " ☆" + repository.getStargazersCount());
				reply.add("\n");
				reply.add(repository.getHtmlUrl().toString());
				reply.add("\n");
				reply.add(truncate(repository.getDescription()));
			} else {
				reply.add("没有找到" + user + "喜欢的项目");
			}
		} catch (Exception exception) {
			log.debug("插件异常: ", exception);
			reply.add("GitHub插件异常");
		}
		return reply;
	}

	private GHRepository randomOne(List<GHRepository> repositories) {
		if (!repositories.isEmpty()) {
			var distribution = new ZipfDistribution(randomGenerator, repositories.size(), 0.5);
			// var distribution = new UniformIntegerDistribution(1, repositories.size());
			return repositories.get(distribution.sample() - 1);
		}
		return null;
	}

	@NotNull
	private String truncate(@Nullable String text) {
		if (text == null) {
			return "";
		}
		if (text.length() > 500) {
			text = text.substring(0, 490) + " [截断]";
		}
		return text;
	}
}
