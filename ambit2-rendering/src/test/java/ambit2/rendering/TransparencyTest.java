package ambit2.rendering;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

public class TransparencyTest {

	@Test
	public void test() throws IOException {
		Dimension imageSize = new Dimension(200,200);
		Color background = new Color(0x00ffffff,true);//white transparent
		Color color = new Color(0x000000ff,false); //black
		BufferedImage buffer = new BufferedImage(imageSize.width, imageSize.height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = buffer.createGraphics();
		g.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		//fill in the entire rectangle with transparent background
		g.setColor(background);
		g.fillRect(0, 0, imageSize.width, imageSize.height);

		//draw diagonal line
		g.setStroke(new BasicStroke(2));
		g.setColor(color);
		g.drawLine(0,0,200,200);
		
		/**
		 * http://docs.oracle.com/javase/tutorial/2d/advanced/compositing.html 
		 */
		
		//draw an ellipse with transparent inside 
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN , 0f ));
		Ellipse2D atom = new Ellipse2D.Double(50,50,20,20);
		g.fill(atom);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER , 1f ));
		g.draw(atom);

		//draw labels with transparent bounding boxes
		String label = "Msg";
		drawLabel(g, color,label, 20,20);
		drawLabel(g, color, label, 100,100);
		

		//draw labels with transparent bounding boxes (another way)
		FontRenderContext frc = g.getFontRenderContext();
		TextLayout layout = new TextLayout("This is a string", g.getFont(), frc);
		Rectangle2D bounds = layout.getBounds();
		Point2D loc = new Point2D.Double(150-bounds.getWidth()/2.0,150-bounds.getHeight()/2.0);
		bounds.setRect(bounds.getX()+loc.getX(),
		                  bounds.getY()+loc.getY(),
		                  bounds.getWidth(),
		                  bounds.getHeight());
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN , 0f ));
		g.setColor(background);
		g.fill(bounds);

		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f ));
		g.setBackground(background);
		g.setColor(color);
		layout.draw(g, (float)loc.getX()-1, (float)loc.getY()+1);

		ImageIO.write(buffer,"png",new File("test.png"));
	}
	
	public void drawLabel(Graphics2D g,  Color color, String label, int x, int y) {
		//transparent bounding box 
		Dimension bbox = getBoundingBox(g,label);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN , 0f ));
		g.fill(new Rectangle2D.Double(Math.round(x - bbox.width/2.0)-1,Math.round(y - bbox.height/2.0)+1,bbox.width+2,bbox.height+2));
		
		//opaque pixels
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f ));
		g.setColor(color);
		g.drawString(label,Math.round(x - bbox.width/2.0)-1,Math.round(y + bbox.height/2.0)-2);
	}
	public Dimension getBoundingBox(Graphics2D g,String msg) {
		// get metrics from the graphics
		FontMetrics metrics = g.getFontMetrics(g.getFont());
		// get the height of a line of text in this
		// font and render context
		int hgt = metrics.getHeight();
		// get the advance of my text in this font
		// and render context
		int adv = metrics.stringWidth(msg);
		return new Dimension(adv,hgt);
	}
}
