package kaba4cow.cpm;

import java.util.prefs.Preferences;

public class Settings {

	private static Preferences preferences;

	private Settings() {

	}

	public static void init() {
		preferences = Preferences.userNodeForPackage(Settings.class);
	}

	public static String getImportDirectory() {
		return preferences.get("dir0", System.getProperty("user.dir"));
	}

	public static void setOpenDirectory(String directory) {
		preferences.put("dir0", directory);
	}

	public static String getExportDirectory() {
		return preferences.get("dir1", System.getProperty("user.dir"));
	}

	public static void setExportDirectory(String directory) {
		preferences.put("dir1", directory);
	}

}
