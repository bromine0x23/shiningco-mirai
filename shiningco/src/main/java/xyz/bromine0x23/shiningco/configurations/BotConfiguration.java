package xyz.bromine0x23.shiningco.configurations;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.bromine0x23.shiningco.listeners.ShiningcoListener;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(BotProperties.class)
public class BotConfiguration {

	private final BotProperties properties;

	public BotConfiguration(BotProperties properties) {
		this.properties = properties;
	}

	@Bean
	public Bot bot(ShiningcoListener listener) throws DecoderException {
		var configuration = new net.mamoe.mirai.utils.BotConfiguration();

		//configuration.enableContactCache();
		configuration.fileBasedDeviceInfo("classpath:device.json");

		Bot bot;
		if (properties.getPasswordMd5() != null) {

			bot = BotFactory.INSTANCE.newBot(properties.getAccount(), Hex.decodeHex(properties.getPasswordMd5()), configuration);
		} else {
			bot = BotFactory.INSTANCE.newBot(properties.getAccount(), properties.getPassword(), configuration);
		}

		bot.login();

		bot.getEventChannel().registerListenerHost(listener);

		return bot;
	}

}
