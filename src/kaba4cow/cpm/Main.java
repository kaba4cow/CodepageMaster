package kaba4cow.cpm;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main extends JFrame implements ActionListener, ChangeListener {

	private static final long serialVersionUID = 1L;

	private final JFileChooser fileOpenChooser;
	private final JFileChooser fileExportChooser;

	private final JCheckBox squareCheckBox;

	private final HashMap<String, Font> fontMap;

	private final DefaultComboBoxModel<String> comboBoxModel;
	private final JComboBox<String> fontComboBox;
	private final JButton browseButton;
	private final JButton exportButton;
	private final JLabel fontLabel;

	private final JSpinner glyphWidthSpinner;
	private final JSpinner glyphHeightSpinner;
	private final JSpinner fontSizeSpinner;

	private Font currentFont;
	private BufferedImage fontImage;

	public Main() {
		super("Codepage Master");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(850, 550);
		setLayout(null);
		setLocationRelativeTo(null);
		setResizable(false);

		File homeFile = new File(Settings.getImportDirectory());
		if (!homeFile.exists() || !homeFile.isDirectory())
			Settings.setOpenDirectory(System.getProperty("user.dir"));

		fileOpenChooser = new JFileChooser(Settings.getImportDirectory());
		fileOpenChooser.setAcceptAllFileFilterUsed(false);
		fileOpenChooser.setFileFilter(new FileNameExtensionFilter("TrueType Fonts", "ttf"));

		fileExportChooser = new JFileChooser(Settings.getExportDirectory());
		fileOpenChooser.setAcceptAllFileFilterUsed(false);
		fileExportChooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG Image", "png"));
		fileExportChooser.addChoosableFileFilter(new FileNameExtensionFilter("JPG Image", "jpg"));
		fileExportChooser.addChoosableFileFilter(new FileNameExtensionFilter("BMP Image", "bmp"));

		JLabel glyphSizeLabel = new JLabel("Glyph:");
		glyphSizeLabel.setLocation(512, 25);
		glyphSizeLabel.setSize(100, 25);
		glyphWidthSpinner = new JSpinner(new SpinnerNumberModel(16, 8, 256, 1));
		glyphWidthSpinner.addChangeListener(this);
		glyphWidthSpinner.setLocation(612, 25);
		glyphWidthSpinner.setSize(100, 25);
		glyphHeightSpinner = new JSpinner(new SpinnerNumberModel(16, 8, 256, 1));
		glyphHeightSpinner.addChangeListener(this);
		glyphHeightSpinner.setLocation(712, 25);
		glyphHeightSpinner.setSize(100, 25);

		JLabel fontSizeLabel = new JLabel("Font:");
		fontSizeLabel.setLocation(512, 50);
		fontSizeLabel.setSize(100, 25);
		fontSizeSpinner = new JSpinner(new SpinnerNumberModel(16, 1, 256, 1));
		fontSizeSpinner.addChangeListener(this);
		fontSizeSpinner.setLocation(612, 50);
		fontSizeSpinner.setSize(200, 25);

		fontLabel = new JLabel();
		fontLabel.setLocation(0, 0);
		fontLabel.setSize(512, 512);

		squareCheckBox = new JCheckBox("Square Image");
		squareCheckBox.setLocation(512, 75);
		squareCheckBox.setSize(300, 25);
		squareCheckBox.addActionListener(this);

		fontMap = new HashMap<>();

		comboBoxModel = new DefaultComboBoxModel<>();
		fontComboBox = new JComboBox<>(comboBoxModel);
		fontComboBox.setLocation(512, 0);
		fontComboBox.setSize(275, 25);
		Font[] fonts = FontGenerator.getAvailableFonts();
		for (int i = 0; i < fonts.length; i++) {
			String name = fonts[i].getName();
			fontComboBox.addItem(name);
			fontMap.put(name, fonts[i]);
		}
		fontComboBox.addActionListener(this);
		currentFont = fonts[0];
		updateFont();

		browseButton = new JButton("...");
		browseButton.setLocation(787, 0);
		browseButton.setSize(25, 25);
		browseButton.addActionListener(this);

		exportButton = new JButton("Export");
		exportButton.setLocation(512, 100);
		exportButton.setSize(300, 25);
		exportButton.addActionListener(this);

		add(glyphSizeLabel);
		add(glyphWidthSpinner);
		add(glyphHeightSpinner);
		add(fontSizeLabel);
		add(fontSizeSpinner);
		add(fontComboBox);
		add(fontLabel);
		add(browseButton);
		add(exportButton);
		add(squareCheckBox);

		setVisible(true);
	}

	@Override
	public void stateChanged(ChangeEvent event) {
		updateFont();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == squareCheckBox) {
			updateFont();
		} else if (source == browseButton) {
			int option = fileOpenChooser.showOpenDialog(this);
			File file = fileOpenChooser.getSelectedFile();
			Settings.setOpenDirectory(file.getParent());
			if (option == JFileChooser.APPROVE_OPTION) {
				Font font = FontGenerator.registerFont(file);
				if (font == null)
					return;
				String name = font.getName();
				fontMap.put(name, font);
				currentFont = font;
				int index = comboBoxModel.getIndexOf(name);
				if (index == -1) {
					fontComboBox.addItem(name);
					fontComboBox.setSelectedIndex(fontComboBox.getItemCount() - 1);
				} else
					fontComboBox.setSelectedIndex(index);
			}
		} else if (source == exportButton) {
			String filename = currentFont.getName().replaceAll("[^-_.A-Za-z0-9]", "_");
			fileExportChooser.setSelectedFile(new File(Settings.getExportDirectory() + "/" + filename + ".png"));
			int option = fileExportChooser.showSaveDialog(this);
			File file = fileExportChooser.getSelectedFile();
			Settings.setExportDirectory(file.getParent());
			if (option == JFileChooser.APPROVE_OPTION) {
				String format;
				filename = file.getName();
				if (!filename.contains("."))
					format = "png";
				else
					format = filename.substring(filename.lastIndexOf('.') + 1);
				updateFont();
				try {
					ImageIO.write(fontImage, format, file);
				} catch (IOException e) {
				}
			}
		} else if (source == fontComboBox) {
			String name = (String) fontComboBox.getSelectedItem();
			currentFont = fontMap.get(name);
			updateFont();
		}
	}

	private void updateFont() {
		fontImage = FontGenerator.createSpriteFont(currentFont, Font.PLAIN, getGlyphWidth(), getGlyphHeight(),
				getFontSize());
		if (fontImage == null)
			fontImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Image scaledImage = fontImage.getScaledInstance(512, 512, Image.SCALE_FAST);
		ImageIcon icon = new ImageIcon(scaledImage);
		if (squareCheckBox.isSelected()) {
			int size = fontImage.getWidth() > fontImage.getHeight() ? fontImage.getWidth() : fontImage.getHeight();
			scaledImage = fontImage.getScaledInstance(size, size, Image.SCALE_FAST);
			fontImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = fontImage.createGraphics();
			graphics.drawImage(scaledImage, 0, 0, null);
			graphics.dispose();
		}
		fontLabel.setIcon(icon);
	}

	private int getGlyphWidth() {
		return ((Number) glyphWidthSpinner.getValue()).intValue();
	}

	private int getGlyphHeight() {
		return ((Number) glyphHeightSpinner.getValue()).intValue();
	}

	private int getFontSize() {
		return ((Number) fontSizeSpinner.getValue()).intValue();
	}

	public static void main(String[] args) {
		Settings.init();
		new Main();
	}

}
