package abc.org.com;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
	public static int[] types = new int[] { 320, 360, 400, 420, 426, 460, 470,
			480, 600, 720, 736, 768, 800, 854, 960, 1024, 1152, 1200, 1280,
			1536, 1600, 1920, 2560 };

	/**
	 * land is base 160 port is base 320
	 */
	public static int base = 160;
	public static String baseStr = "<dimen name=\"dimen_%sdp\">%sdip</dimen>";
	public static String baseStrSP = "<dimen name=\"dimen_%ssp\">%ssp</dimen>";
	public static final String BASE_NAME = "values-w%sdp%s/dimens-m1.xml";
	public static final String BASE_FOLDER = "values-w%sdp%s";

	public static void main(String[] args) {
		String screen_type_land = "-land";
		// port
		String screen_type_port = "-port";
		for (int type : types) {
			write(type, screen_type_land);
			write(type, screen_type_port);
		}
	}

	/**
	 * 
	 * @param skinsScreen
	 *            ldpi 120 mdpi 160 hdpi 240 xhdpi 320
	 * @param type
	 * @param screen_type
	 *            land port
	 */

	private static void write(int type, String screen_type) {
		int base = 120;

		// if ("ldpi".equals(skinsScreen)) {
		// base = 120;
		// } else if ("mdpi".equals(skinsScreen)) {
		// base = 160;
		// } else if ("hdpi".equals(skinsScreen)) {
		// base = 240;
		// } else if ("xhdpi".equals(skinsScreen)) {
		// base = 320;
		// }

		if ("-land".equals(screen_type)) {
			base = 320;
		} else {
			base = 320;
		}
		float scale = (float) type / (float) base / 2;
		StringBuilder builder = new StringBuilder();
		builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		builder.append("\n");
		builder.append("<resources>").append("\n");
		for (int i = 0; i <= 1024; i++) {
			builder.append("\t");
			builder.append(String.format(baseStr, i, (int) (i * scale)));
			builder.append("\n");

			builder.append("\t");
			builder.append(String.format(baseStrSP, i, (int) (i * scale)));
			builder.append("\n");
		}
		builder.append("</resources>");
		builder.append("\n");
		String stype = type + "";
		File file = new File(String.format(BASE_FOLDER, stype, screen_type));

		if (!file.exists()) {
			file.mkdir();
		}

		file = new File(String.format(BASE_NAME, stype, screen_type));
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
			}
		}
		write(String.format(BASE_NAME, stype, screen_type), builder.toString());

	}

	private static void write(String file, String string) {
		try {
			PrintWriter out = new PrintWriter(file);
			out.println(string);
			out.close();
		} catch (FileNotFoundException e) {
		}
	}
}
