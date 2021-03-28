package xyz.bromine0x23.shiningco.plugins.netease;

import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

public class NeteaseEncryptor {

	private static final String AES_KEY      = "0CoJUm6Qyw8W8jud";
	private static final String AES_IV       = "0102030405060708";
	private static final String RSA_EXPONENT = "010001";
	private static final String RSA_MODULUS  = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7";

	private final SecretKey       aesKey;
	private final IvParameterSpec aesIv;
	private final BigInteger      rsaExponent;
	private final BigInteger      rsaModulus;

	public NeteaseEncryptor() {
		this.aesKey      = new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.US_ASCII), "AES");
		this.aesIv       = new IvParameterSpec(AES_IV.getBytes(StandardCharsets.US_ASCII));
		this.rsaExponent = new BigInteger(RSA_EXPONENT, 16);
		this.rsaModulus  = new BigInteger(RSA_MODULUS, 16);
	}

	@SneakyThrows
	public MultiValueMap<String, String> weapiEncrypt(String data) {
//			var secretKeyBytes = new byte[]{
//				0x20, 0x2E, 0x20, 0x2E, 0x20, 0x2E, 0x20, 0x2E,
//				0x20, 0x2E, 0x20, 0x2E, 0x20, 0x2E, 0x20, 0x2E,
//			};
		var secretKey = RandomStringUtils.randomAlphanumeric(16).getBytes(StandardCharsets.US_ASCII);
		var encryptedData = encryptData(
			encryptData(data, aesKey, aesIv), new SecretKeySpec(secretKey, "AES"), aesIv
		);
		var encryptedSecretKey = encryptSecretKey(secretKey, rsaExponent, rsaModulus);

		var body = new LinkedMultiValueMap<String, String>();
		body.add("params", encryptedData);
		body.add("encSecKey", encryptedSecretKey);
		return body;
	}

	private static String encryptData(String data, SecretKey secretKey, IvParameterSpec iv) {
		var encryptedData = aesEncrypt(data.getBytes(StandardCharsets.UTF_8), secretKey, iv);
		return Base64.getEncoder().encodeToString(encryptedData);
	}

	public static String encryptSecretKey(byte[] secretKey, BigInteger exponent, BigInteger modules) {
		var encrypted = rsaEncrypt(reverse(secretKey), exponent, modules);
		return encrypted.toString(16);
	}

	@SneakyThrows
	private static byte[] aesEncrypt(byte[] data, SecretKey key, AlgorithmParameterSpec parameter) {
		var cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, parameter);
		return cipher.doFinal(data);
	}

	@SneakyThrows
	private static BigInteger rsaEncrypt(byte[] data, BigInteger exponent, BigInteger modules) {
		var encrypted = new BigInteger(data);
		encrypted = encrypted.modPow(exponent, modules);
		return encrypted;
	}

	private static byte[] reverse(byte[] bytes) {
		byte[] reversed = new byte[bytes.length];
		for (int i = 0; i < bytes.length; ++i) {
			reversed[i] = bytes[bytes.length - i - 1];
		}
		return reversed;
	}

}
