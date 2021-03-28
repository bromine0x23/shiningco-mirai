package xyz.bromine0x23.shiningco.plugins.github;

import lombok.SneakyThrows;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.HttpConnector;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class GithubService {

	private final GitHub github;

	public GithubService() throws IOException {
		this.github = new GitHubBuilder()
			.withConnector(HttpConnector.DEFAULT)
			.build();
	}

	@Retryable
	public List<GHRepository> searchRepositories(String keyword) {
		return github.searchRepositories()
			.q(keyword)
			.list()
			.iterator()
			.nextPage();
	}

	@Retryable
	public List<GHRepository> searchRepositoriesByUser(String user) {
		return github.searchRepositories()
			.user(user)
			.list()
			.iterator()
			.nextPage();
	}

	@SneakyThrows
	@Retryable
	public List<GHRepository> searchRepositoriesByUserStarred(String login) {
		var user = github.getUser(login);
		if (user != null) {
			return user
				.listStarredRepositories()
				.iterator()
				.nextPage();
		}
		return Collections.emptyList();
	}

}
