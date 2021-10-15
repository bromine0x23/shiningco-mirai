package xyz.bromine0x23.shiningco.plugins.pixiv;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.Proxy;

@ConfigurationProperties("shiningco.plugins.pixiv")
@Data
public class PixivPluginProperties {

	private String apiKey;

	private String apiSecret;

	private String refreshToken;

}
