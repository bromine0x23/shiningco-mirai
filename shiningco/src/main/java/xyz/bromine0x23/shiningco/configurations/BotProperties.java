package xyz.bromine0x23.shiningco.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("bot")
public class BotProperties {

	private long account;

	private String password;

	private String passwordMd5;

}
