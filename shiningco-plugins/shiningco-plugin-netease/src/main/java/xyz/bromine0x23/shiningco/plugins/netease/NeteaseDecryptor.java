package xyz.bromine0x23.shiningco.plugins.netease;

import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;

public class NeteaseDecryptor {
	// The inverse AES S-box
	private static final byte[] Si          = {
		(byte) 82, (byte) 9, (byte) 106, (byte) 213, (byte) 48, (byte) 54, (byte) 165, (byte) 56,
		(byte) 191, (byte) 64, (byte) 163, (byte) 158, (byte) 129, (byte) 243, (byte) 215, (byte) 251,
		(byte) 124, (byte) 227, (byte) 57, (byte) 130, (byte) 155, (byte) 47, (byte) 255, (byte) 135,
		(byte) 52, (byte) 142, (byte) 67, (byte) 68, (byte) 196, (byte) 222, (byte) 233, (byte) 203,
		(byte) 84, (byte) 123, (byte) 148, (byte) 50, (byte) 166, (byte) 194, (byte) 35, (byte) 61,
		(byte) 238, (byte) 76, (byte) 149, (byte) 11, (byte) 66, (byte) 250, (byte) 195, (byte) 78,
		(byte) 8, (byte) 46, (byte) 161, (byte) 102, (byte) 40, (byte) 217, (byte) 36, (byte) 178,
		(byte) 118, (byte) 91, (byte) 162, (byte) 73, (byte) 109, (byte) 139, (byte) 209, (byte) 37,
		(byte) 114, (byte) 248, (byte) 246, (byte) 100, (byte) 134, (byte) 104, (byte) 152, (byte) 22,
		(byte) 212, (byte) 164, (byte) 92, (byte) 204, (byte) 93, (byte) 101, (byte) 182, (byte) 146,
		(byte) 108, (byte) 112, (byte) 72, (byte) 80, (byte) 253, (byte) 237, (byte) 185, (byte) 218,
		(byte) 94, (byte) 21, (byte) 70, (byte) 87, (byte) 167, (byte) 141, (byte) 157, (byte) 132,
		(byte) 144, (byte) 216, (byte) 171, (byte) 0, (byte) 140, (byte) 188, (byte) 211, (byte) 10,
		(byte) 247, (byte) 228, (byte) 88, (byte) 5, (byte) 184, (byte) 179, (byte) 69, (byte) 6,
		(byte) 208, (byte) 44, (byte) 30, (byte) 143, (byte) 202, (byte) 63, (byte) 15, (byte) 2,
		(byte) 193, (byte) 175, (byte) 189, (byte) 3, (byte) 1, (byte) 19, (byte) 138, (byte) 107,
		(byte) 58, (byte) 145, (byte) 17, (byte) 65, (byte) 79, (byte) 103, (byte) 220, (byte) 234,
		(byte) 151, (byte) 242, (byte) 207, (byte) 206, (byte) 240, (byte) 180, (byte) 230, (byte) 115,
		(byte) 150, (byte) 172, (byte) 116, (byte) 34, (byte) 231, (byte) 173, (byte) 53, (byte) 133,
		(byte) 226, (byte) 249, (byte) 55, (byte) 232, (byte) 28, (byte) 117, (byte) 223, (byte) 110,
		(byte) 71, (byte) 241, (byte) 26, (byte) 113, (byte) 29, (byte) 41, (byte) 197, (byte) 137,
		(byte) 111, (byte) 183, (byte) 98, (byte) 14, (byte) 170, (byte) 24, (byte) 190, (byte) 27,
		(byte) 252, (byte) 86, (byte) 62, (byte) 75, (byte) 198, (byte) 210, (byte) 121, (byte) 32,
		(byte) 154, (byte) 219, (byte) 192, (byte) 254, (byte) 120, (byte) 205, (byte) 90, (byte) 244,
		(byte) 31, (byte) 221, (byte) 168, (byte) 51, (byte) 136, (byte) 7, (byte) 199, (byte) 49,
		(byte) 177, (byte) 18, (byte) 16, (byte) 89, (byte) 39, (byte) 128, (byte) 236, (byte) 95,
		(byte) 96, (byte) 81, (byte) 127, (byte) 169, (byte) 25, (byte) 181, (byte) 74, (byte) 13,
		(byte) 45, (byte) 229, (byte) 122, (byte) 159, (byte) 147, (byte) 201, (byte) 156, (byte) 239,
		(byte) 160, (byte) 224, (byte) 59, (byte) 77, (byte) 174, (byte) 42, (byte) 245, (byte) 176,
		(byte) 200, (byte) 235, (byte) 187, (byte) 60, (byte) 131, (byte) 83, (byte) 153, (byte) 97,
		(byte) 23, (byte) 43, (byte) 4, (byte) 126, (byte) 186, (byte) 119, (byte) 214, (byte) 38,
		(byte) 225, (byte) 105, (byte) 20, (byte) 99, (byte) 85, (byte) 33, (byte) 12, (byte) 125,
	};
	private static final int    BLOCK_SIZE  = 64;
	private static final int    KEY_SIZE    = 64;
	private static final int    LENGTH_SIZE = 4;
	private static final String KEY         = "fuck~#$%^&*(458";

	private final byte[] key;

	public NeteaseDecryptor() {
		this.key = fillKey(KEY.getBytes(StandardCharsets.US_ASCII));
	}

	@SneakyThrows
	public String abroadDecrypt(String data) {
		byte[] bytes     = Hex.decodeHex(data);
		var    decrypted = decryptBlocks(bytes, key);
		return new String(decrypted, StandardCharsets.UTF_8);
	}

	public static byte[] decryptBlocks(byte[] data, byte[] key) {
		var blocks = split(data);
		var buffer = new byte[BLOCK_SIZE * blocks.length];
		var iv     = key;
		for (var i = 0; i < blocks.length; ++i) {
			var decrypted = xor(subtract(xor(substitution(substitution(blocks[i])), iv), iv), key);
			System.arraycopy(decrypted, 0, buffer, i * BLOCK_SIZE, BLOCK_SIZE);
			iv = blocks[i];
		}
		var lengthBytes = new byte[LENGTH_SIZE];
		System.arraycopy(buffer, buffer.length - LENGTH_SIZE, lengthBytes, 0, LENGTH_SIZE);

		var output = new byte[bytesToIntBE(lengthBytes)];
		System.arraycopy(buffer, 0, output, 0, output.length);
		return output;
	}

	// 分割 Block
	private static byte[][] split(byte[] data) {
		var blocks = new byte[data.length / BLOCK_SIZE][];
		var di     = 0;
		for (var i = 0; i < blocks.length; ++i) {
			blocks[i] = new byte[BLOCK_SIZE];
			for (var j = 0; j < BLOCK_SIZE; ++j) {
				blocks[i][j] = data[di++];
			}
		}
		return blocks;
	}

	// 填充密钥
	public static byte[] fillKey(byte[] key) {
		var filled = new byte[KEY_SIZE];
		if (key != null && key.length != 0) {
			if (key.length >= KEY_SIZE) {
				System.arraycopy(key, 0, filled, 0, KEY_SIZE);
			} else {
				for (var i = 0; i < KEY_SIZE; ++i) {
					filled[i] = key[i % key.length];
				}
			}
		}
		return filled;
	}

	// 替换
	private static byte[] substitution(byte[] input) {
		var output = new byte[input.length];
		for (int i = 0; i < input.length; ++i) {
			var x = input[i];
			output[i] = Si[x < 0 ? 256 + x : x];
		}
		return output;
	}

	// 块异或
	public static byte[] xor(byte[] l, byte[] r) {
		var output = new byte[l.length];
		for (var i = 0; i < l.length; ++i) {
			output[i] = (byte) (l[i] ^ r[i % r.length]);
		}
		return output;
	}

	// 相减
	public static byte[] subtract(byte[] l, byte[] r) {
		var output = new byte[l.length];
		for (int i = 0; i < l.length; ++i) {
			output[i] = (byte) ((l[i] - r[i % r.length]) & 0xFF);
		}
		return output;
	}

	public static int bytesToIntBE(byte[] bytes) {
		var i = 0;
		i += (bytes[0] & 255) << 24;
		i += (bytes[1] & 255) << 16;
		i += (bytes[2] & 255) << 8;
		i += bytes[3] & 255;
		return i;
	}

}
