package xyz.bromine0x23.shiningco.runtime;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.Proxy;

@ConfigurationProperties("shiningco.proxy")
@Data
public class ProxyProperties {

	private Proxy.Type type = Proxy.Type.DIRECT;

	private String host;

	private int port;
}
