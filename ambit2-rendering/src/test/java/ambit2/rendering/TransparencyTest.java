package ambit2.rendering;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.ZoomFactor;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

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
		drawLabel(g, color,label+"+", 20,20);
		drawLabel(g, color, label+"2", 100,100);
		

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
		int p = label.length();
		//transparent bounding box 
		AttributedString as = new AttributedString(label);
		as.addAttribute(TextAttribute.SUPERSCRIPT,TextAttribute.SUPERSCRIPT_SUPER,p-1,p);
		Dimension bbox = getBoundingBox(g,label);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN , 0f ));
		g.fill(new Rectangle2D.Double(Math.round(x - bbox.getWidth()/2.0)-1,Math.round(y - bbox.getHeight()/2.0)+1,bbox.getWidth()+2,bbox.getHeight()+2));
		
		//opaque pixels
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f ));
		g.setColor(color);
		g.drawString(as.getIterator(),Math.round(x - bbox.getWidth()/2.0)-1,Math.round(y + bbox.getHeight()/2.0)-2);
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
		//return new Dimension(adv,hgt);
	}
	


		@Test
		public void testDepict() throws Exception {
			String smiles = "CN1C=NC2=C1C(=O)N(C(=O)N2C)C";
			int WIDTH = 200;
			int HEIGHT = 250;
			Rectangle drawArea = new Rectangle(WIDTH, HEIGHT);
			BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
			SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
			IAtomContainer molecule = smilesParser.parseSmiles(smiles);
			StructureDiagramGenerator sdg =  new StructureDiagramGenerator();
			sdg.setMolecule(molecule);
			sdg.generateCoordinates();
			molecule = sdg.getMolecule();
			List generators =  new ArrayList();
			generators.add(new BasicSceneGenerator());
			generators.add(new BasicBondGenerator());
			generators.add(new BasicAtomGenerator());
			AtomContainerRenderer renderer = new AtomContainerRenderer(generators, new AWTFontManager());
			renderer.setup(molecule, drawArea);
			RendererModel model = renderer.getRenderer2DModel();
			model.set(ZoomFactor.class, (double)0.9);
			Graphics2D g2 = (Graphics2D)image.getGraphics();
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, WIDTH, HEIGHT);
			renderer.paint(molecule, new AWTDrawVisitor(g2));
			ImageIO.write( image, "PNG", new File("depict.png"));
		}
	
}
