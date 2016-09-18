package jmedialayer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class FileUtils {
	static public byte[] read(File file) throws IOException {
		byte[] out = new byte[(int) file.length()];
		FileInputStream s = new FileInputStream(file);
		int readed = s.read(out);
		s.close();

		return (readed != out.length) ? Arrays.copyOf(out, readed) : out;
	}
}
