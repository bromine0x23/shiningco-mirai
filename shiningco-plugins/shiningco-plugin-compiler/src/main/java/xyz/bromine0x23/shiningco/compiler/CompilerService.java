package xyz.bromine0x23.shiningco.compiler;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import xyz.bromine0x23.shiningco.compiler.representations.BuildResult;
import xyz.bromine0x23.shiningco.compiler.representations.Request;

import java.util.Map;

@Service
public class CompilerService {

	private static final Map<String, String> COMPILERS = Map.of(
		"c", "cg102",
		"c++", "g102",
		"go", "gl1150",
		"rust", "r1500"
	);

	private final RestTemplate restTemplate;

	private final RateLimiter rateLimiter = RateLimiter.create(1);

	public CompilerService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}


	public BuildResult compileAndExecute(String language, String source) {
		var compiler = COMPILERS.get(language);
		var request = Request.builder()
			.lang(language)
			.compiler(compiler)
			.source(source)
			.build()
			;
		rateLimiter.acquire();
		return restTemplate.postForObject("https://gcc.godbolt.org/api/compiler/{compiler}/compile", request, BuildResult.class, compiler);
	}
}
