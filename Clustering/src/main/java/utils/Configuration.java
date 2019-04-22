package utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public abstract class Configuration {

	private static final int MIN_PORT = 1000;
	private static final int MAX_PORT = 65535;
	private static int DEFAULT_PORT = 12340;


	private static Path LOG_PATH = null;

	public static int getMinPort() {
		return MIN_PORT;
	}

	public static int getMaxPort() {
		return MAX_PORT;
	}

	public static int getDefaultPort() {
		return DEFAULT_PORT;
	}

	public static Path getLogPath() {
		return LOG_PATH;
	}

	public static void loadConfig() {
		Reader reader = null;
		Properties prop = new Properties();

		try {

			reader = new FileReader("config.properties");
			prop.load(reader);

			try {

				// Ziehen des aktuellen Verzeichnisses
				File f = new File(
						Configuration.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

				Path path = Paths.get(new File(f.getParent() + "/log.txt").getAbsolutePath());

				LOG_PATH = path;

			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

			String value = prop.getProperty("DEFAULT_PORT", "12340");
			if (value != null && !value.isEmpty()) {
				try {
					DEFAULT_PORT = Integer.parseInt(value);
				} catch (NumberFormatException ex) {
					DEFAULT_PORT = 12340;
				}
			}

			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
