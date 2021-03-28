package xyz.bromine0x23.shiningco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

import java.net.InetSocketAddress;
import java.net.Proxy;

@SpringBootApplication
@EnableRetry
public class ShiningcoApplication {


	public static final String PROXY_HOST = "127.0.0.1";
	public static final int    PROXY_PORT = 1080;
	public static final Proxy  PROXY      = new Proxy(java.net.Proxy.Type.HTTP, InetSocketAddress.createUnresolved(PROXY_HOST, PROXY_PORT));

	public static void main(String[] args) {
		SpringApplication.run(ShiningcoApplication.class, args);
	}

}
