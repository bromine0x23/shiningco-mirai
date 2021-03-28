package xyz.bromine0x23.shiningco.plugins.pixiv;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.bromine0x23.shiningco.plugins.pixiv.representations.Illustration;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PixivService {

	private final PixivApiService apiService;

	private final LoadingCache<String, byte[]> imageCache;

	public PixivService(
		PixivApiService apiService
	) {
		this.apiService = apiService;
		this.imageCache = CacheBuilder.newBuilder()
			.expireAfterAccess(Duration.ofMinutes(5))
			.build(CacheLoader.from(apiService::download));
	}

	public List<Illustration> searchRandomOneByKeyword(String keyword, boolean restricted) {
		return select(apiService.searchIllust(keyword), restricted);
	}

	public List<Illustration> searchRandomOneByArtist(String artist, boolean restricted) {
		return select(apiService.searchIllustByUser(artist), restricted);
	}

	@SneakyThrows
	public byte[] download(String uri) {
		return imageCache.get(uri);
	}

	private List<Illustration> select(List<Illustration> illustrations, boolean restricted) {
		return illustrations.stream()
			.filter(illustration -> illustration.getTags().stream().noneMatch(tag -> Objects.equals("R-18G", tag.getName())))
			.filter(illustration -> {
				var time = LocalTime.now();
				return (time.getHour() < 8 || 20 <= time.getHour()) || restricted == (illustration.getSanityLevel() > 5);
			})
			.collect(Collectors.toList());
	}

}
