package tests;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

import simplestream.Compressor;

public class CompressionAndEncodingTests {


	private boolean byteArraysEqual(byte[] b1, byte[] b2) {
		for (int i = 0; i < b1.length; i++) {
			boolean elementsEqual;
			try {
				elementsEqual = (b1[i] == b2[i]);
			} catch (ArrayIndexOutOfBoundsException e) {
				return false;
			}

			if (!elementsEqual) {
				return false;
			}
		}
		return true;
	}


	@Test
	public void compressSimpleArray() {
		byte[] intialBytes = new byte[2096];

		byte[] compressed = Compressor.compress(intialBytes);
		byte[] decompressed = Compressor.decompress(compressed);

		assertTrue(byteArraysEqual(intialBytes, decompressed));
	}

	@Test
	public void compressString() {
		String intialString = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

		byte[] bytes = null;
		try {
			bytes = intialString.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			Assert.fail();
		}

		byte[] compressed = Compressor.compress(bytes);
		byte[] decompressed = Compressor.decompress(compressed);

		String resultString = null;
		try {
			resultString = new String(decompressed, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Assert.fail();
		}

		// System.out.println(resultString);
		assertTrue(intialString.equals(resultString));
	}

	@Test
	public void encodeAndCompressString() {
		String intialString =
			"somelongstupidstring..fasdjfhasjdkfasdjfhasdf/asdfhasdfasdhfasdhjkfahsdjkfahskdf";

		byte[] bytes = null;
		try {
			bytes = intialString.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			Assert.fail();
		}

		byte[] encoded = Base64.encodeBase64(bytes);

		byte[] compressed = Compressor.compress(encoded);
		byte[] decompressed = Compressor.decompress(compressed);

		byte[] decoded = Base64.decodeBase64(decompressed);

		String resultString = null;
		try {
			resultString = new String(decoded, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Assert.fail();
		}

		// System.out.println(resultString);
		assertTrue(intialString.equals(resultString));
	}

}
