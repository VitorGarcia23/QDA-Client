package fatec.rest.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileHelper {

	public static boolean eraseFile(String filePath) throws IOException {
		File file = new File(filePath);
		return Files.deleteIfExists(file.toPath());
	}

}
