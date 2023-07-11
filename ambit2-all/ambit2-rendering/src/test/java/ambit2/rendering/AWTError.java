package ambit2.rendering;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.junit.Test;

import junit.framework.Assert;

public class AWTError {
	//this will throw error if fontconfig is broken (some OpenJDK packages)
	@Test
	public void test() throws Exception {

		String text = "abcXYZ123";
		int x = 5;
		int y = 25;
		int height = 30;
		int fontsize = 24;

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		BufferedImage img = new BufferedImage(text.length() * 20, height, BufferedImage.TYPE_INT_RGB);
		img.createGraphics();
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(Color.gray);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		Font font = new Font("Monospaced", Font.BOLD + Font.ITALIC, fontsize);

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g.setFont(font);
		g.setColor(Color.gray.darker());

		char[] c = text.toCharArray();

		for (int i = 0; i < c.length; i++) {
			char[] c1 = new char[1];
			c1[0] = c[i];
			g.drawChars(c1, 0, 1, x + (i * 16), y);
		}

		ImageIO.write(img, "png", outStream);
		Assert.assertTrue("Png size greater than 0", outStream.size()>0);

	}

}
