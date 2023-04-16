package kaba4cow.cpm;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FontGenerator {

	private static final char[] CHARSET = { '\u0000', '\u263A', '\u263B', '\u2665', '\u2666', '\u2663', '\u2660',
			'\u2022', '\u25D8', '\u25CB', '\u25D9', '\u2642', '\u2640', '\u266A', '\u266B', '\u263C', '\u25BA',
			'\u25C4', '\u2195', '\u203C', '\u00B6', '\u00A7', '\u25AC', '\u21A8', '\u2191', '\u2193', '\u2192',
			'\u2190', '\u221F', '\u2194', '\u25B2', '\u25BC', '\u0020', '\u0021', '\u0022', '\u0023', '\u0024',
			'\u0025', '\u0026', '\'', '\u0028', '\u0029', '\u002A', '\u002B', '\u002C', '\u002D', '\u002E', '\u002F',
			'\u0030', '\u0031', '\u0032', '\u0033', '\u0034', '\u0035', '\u0036', '\u0037', '\u0038', '\u0039',
			'\u003A', '\u003B', '\u003C', '\u003D', '\u003E', '\u003F', '\u0040', '\u0041', '\u0042', '\u0043',
			'\u0044', '\u0045', '\u0046', '\u0047', '\u0048', '\u0049', '\u004A', '\u004B', '\u004C', '\u004D',
			'\u004E', '\u004F', '\u0050', '\u0051', '\u0052', '\u0053', '\u0054', '\u0055', '\u0056', '\u0057',
			'\u0058', '\u0059', '\u005A', '\u005B', '\\', '\u005D', '\u005E', '\u005F', '\u0060', '\u0061', '\u0062',
			'\u0063', '\u0064', '\u0065', '\u0066', '\u0067', '\u0068', '\u0069', '\u006A', '\u006B', '\u006C',
			'\u006D', '\u006E', '\u006F', '\u0070', '\u0071', '\u0072', '\u0073', '\u0074', '\u0075', '\u0076',
			'\u0077', '\u0078', '\u0079', '\u007A', '\u007B', '\u007C', '\u007D', '\u007E', '\u2302', '\u00C7',
			'\u00FC', '\u00E9', '\u00E2', '\u00E4', '\u00E0', '\u00E5', '\u00E7', '\u00EA', '\u00EB', '\u00E8',
			'\u00EF', '\u00EE', '\u00EC', '\u00C4', '\u00C5', '\u00C9', '\u00E6', '\u00C6', '\u00F4', '\u00F6',
			'\u00F2', '\u00FB', '\u00F9', '\u00FF', '\u00D6', '\u00DC', '\u00A2', '\u00A3', '\u00A5', '\u20A7',
			'\u0192', '\u00E1', '\u00ED', '\u00F3', '\u00FA', '\u00F1', '\u00D1', '\u00AA', '\u00BA', '\u00BF',
			'\u2310', '\u00AC', '\u00BD', '\u00BC', '\u00A1', '\u00AB', '\u00BB', '\u2591', '\u2592', '\u2593',
			'\u2502', '\u2524', '\u2561', '\u2562', '\u2556', '\u2555', '\u2563', '\u2551', '\u2557', '\u255D',
			'\u255C', '\u255B', '\u2510', '\u2514', '\u2534', '\u252C', '\u251C', '\u2500', '\u253C', '\u255E',
			'\u255F', '\u255A', '\u2554', '\u2569', '\u2566', '\u2560', '\u2550', '\u256C', '\u2567', '\u2568',
			'\u2564', '\u2565', '\u2559', '\u2558', '\u2552', '\u2553', '\u256B', '\u256A', '\u2518', '\u250C',
			'\u2588', '\u2584', '\u258C', '\u2590', '\u2580', '\u03B1', '\u00DF', '\u0393', '\u03C0', '\u03A3',
			'\u03C3', '\u00B5', '\u03C4', '\u03A6', '\u0398', '\u03A9', '\u03B4', '\u221E', '\u03C6', '\u03B5',
			'\u2229', '\u2261', '\u00B1', '\u2265', '\u2264', '\u2320', '\u2321', '\u00F7', '\u2248', '\u00B0',
			'\u2219', '\u00B7', '\u221A', '\u207F', '\u00B2', '\u25A0', '\u00A0' };

	public FontGenerator(String fontName, int glyphSize, float fontSize, String output) {
		File file = new File(fontName);
		System.out.println(fontName);
		BufferedImage image = createSpriteFont(file.getAbsolutePath(), Font.PLAIN, glyphSize, glyphSize, fontSize);

		try {
			ImageIO.write(image, "PNG", new File("output/" + output + ".png"));
			System.out.println("Finished");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Font[] getAvailableFonts() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		return ge.getAllFonts();
	}

	public static Font registerFont(File file) {
		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			Font font = Font.createFont(Font.TRUETYPE_FONT, file);
			ge.registerFont(font);
			return font;
		} catch (Exception e) {
			return null;
		}
	}

	public static BufferedImage createSpriteFont(String path, int style, int glyphWidth, int glyphHeight,
			float fontSize) {
		File file = new File(path);
		Font font = registerFont(file);
		return createSpriteFont(font, style, glyphWidth, glyphHeight, fontSize);
	}

	public static BufferedImage createSpriteFont(Font font, int style, int glyphWidth, int glyphHeight,
			float fontSize) {
		if (font == null)
			return null;
		font = font.deriveFont(style, fontSize);

		int imageWidth = 16 * glyphWidth;
		int imageHeight = 16 * glyphHeight;

		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(Color.black);
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		graphics.setColor(Color.white);
		graphics.setFont(font);

		int glyphIndex = 0;
		for (int y = 0; y < 16; y++)
			for (int x = 0; x < 16; x++) {
				if (glyphIndex >= CHARSET.length)
					break;
				char glyph = CHARSET[glyphIndex];
				graphics.drawString(Character.toString(glyph), x * glyphWidth, y * glyphHeight + glyphHeight - 1);
				glyphIndex++;
			}
		graphics.dispose();

		return image;
	}

	public static void main(String[] args) throws IOException {
		File dir = new File("fonts");
		File[] list = dir.listFiles();
		for (File file : list) {
			String name = file.getName().replaceAll("Px437_", "").replaceAll("_", " ").replaceAll(".ttf", "");
			new FontGenerator(file.getAbsolutePath(), 8, 8, name);
		}
	}

}
