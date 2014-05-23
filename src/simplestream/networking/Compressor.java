package simplestream.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Performs compression on image data to be sent over the network.
 */
public class Compressor {

	private static final Logger log = Logger.getLogger(Compressor.class);

	/**
	 * Compresses the given bytes using GZIP.
	 *
	 * @param content The data to compress.
	 * @return The compressed data.
	 */
	public static byte[] compress(byte[] content) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
			gzipOutputStream.write(content);
			gzipOutputStream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		log.debug(String.format("Compression ratio %f\n",
			(1.0f * content.length / byteArrayOutputStream.size())));
		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * Decompresses the given bytes using GZIP.
	 *
	 * @param content The data to decompress.
	 * @return The decompressed data.
	 */
	public static byte[] decompress(byte[] contentBytes) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(contentBytes)), out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return out.toByteArray();
	}

	/**
	 * Determines whether data of the given type is worth compressing.
	 *
	 * @param contentType The type of data to decide on compression for.
	 * @return True if the data is not already well-compressed, false otherwise.
	 */
	public static boolean notWorthCompressing(String contentType) {
		return contentType.contains("jpeg") || contentType.contains("pdf")
			|| contentType.contains("zip") || contentType.contains("mpeg")
			|| contentType.contains("avi");
	}

}
