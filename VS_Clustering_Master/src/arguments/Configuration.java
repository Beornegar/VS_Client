package arguments;

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
	private static int serverPort = 12340;

	private static boolean verbose = false;
	private static int maxAmountOfRequests = 0;

	private static Path LOG_PATH = null;

	public static int getMinPort() {
		return MIN_PORT;
	}

	public static int getMaxPort() {
		return MAX_PORT;
	}

	public static int getServerPort() {
		return serverPort;
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

			String value = prop.getProperty("SERVER_PORT", "10000");
			try {
				serverPort = Integer.parseInt(value);
			} catch (NumberFormatException ex) {
				serverPort = 9000;
			}

			value = prop.getProperty("VERBOSE", "false");
			verbose = Boolean.parseBoolean(value);

			value = prop.getProperty("MAX_AMOUNT_OF_REQUESTS", "0");
			try {
				setMaxAmountOfRequests(Integer.parseInt(value));
			} catch (NumberFormatException ex) {
				setMaxAmountOfRequests(0);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static boolean isVerbose() {
		return verbose;
	}

	public static int getMaxAmountOfRequests() {
		return maxAmountOfRequests;
	}

	public static void setMaxAmountOfRequests(int maxAmountOfRequests) {
		Configuration.maxAmountOfRequests = maxAmountOfRequests;
	}

}
