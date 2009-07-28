/*
 * Created on 2006-3-5
 *
 */
package ambit2.core.io;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.renderer.Renderer2D;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.MFAnalyser;

import ambit2.core.processors.structure.StructureTypeProcessor;


/**
 * Generates BufferedImage from smiles or compound.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-3-5
 */
public class CompoundImageTools {
    Renderer2DModel r2dm;
    Renderer2D renderer;
    protected Dimension imageSize = new Dimension(200,200);
    protected Color background = Color.white;
    protected BufferedImage defaultImage = null;
    BufferedImage buffer = null;
    protected Color borderColor = Color.GRAY;
    protected int borderWidth = 5;
    public int getBorderWidth() {
		return borderWidth;
	}
	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
	}
	public Color getBorderColor() {
		return borderColor;
	}
    /**
     * Creates gradient border of this color. Set the same as background color if you don't want shadowing effect.
     * @param borderColor
     */
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}
	protected SmilesParser parser = null;
    /**
     * 
     */
    public CompoundImageTools() {
        this(new Dimension(200,200));
    }
    public CompoundImageTools(Dimension cellSize) {
        super();
        renderer = createRenderer(cellSize,background);
        r2dm = renderer.getRenderer2DModel();
		this.imageSize = cellSize;
    }
    
    private static Renderer2D createRenderer(Dimension cellSize,Color background) {
    	Renderer2DModel r2dm = new Renderer2DModel();

		r2dm.setBackgroundDimension(cellSize);
		r2dm.setBackColor(background);
		r2dm.setForeColor(Color.BLACK);
		r2dm.setDrawNumbers(false);
		r2dm.setUseAntiAliasing(true);
		r2dm.setColorAtomsByType(true);
		r2dm.setShowImplicitHydrogens(false);
		r2dm.setShowAromaticity(true);    	
		return new Renderer2D(r2dm);
    }
    public synchronized BufferedImage getImage(Object o) {
        if (o instanceof IAtomContainer)
            return getImage((IAtomContainer)o);
        else return defaultImage;
    }

    public synchronized BufferedImage getImage(String smiles) {
        ArrayList<String> m = null;
        try {
            if (parser == null) parser = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
            return getImage(parser.parseSmiles(smiles));
        } catch (Exception x) {
            m = new ArrayList<String>();
            m.add(x.getMessage());
        }
        return getImage(m);
    }

	public synchronized BufferedImage getImage(IAtomContainer molecule) {
		return getImage(molecule,null);
	}
	public synchronized BufferedImage getImage(IAtomContainer molecule, IAtomContainer highlighted) {
		return getImage(molecule, highlighted,false);
	}
    public synchronized BufferedImage getImage(IAtomContainer molecule, IAtomContainer highlighted, boolean build2d) {    
    	renderer = createRenderer(imageSize,background);
    	r2dm = renderer.getRenderer2DModel();
        
        if (buffer == null)
            buffer = new BufferedImage(imageSize.width, imageSize.height,
				BufferedImage.TYPE_INT_RGB);
        
		Graphics2D g = buffer.createGraphics();
		g.setColor(background);
		g.fillRect(0, 0, imageSize.width, imageSize.height);
		//g.setColor(Color.black);
		
		IMoleculeSet molecules = new MoleculeSet();
        generate2D(molecule, build2d, molecules);
        paint(renderer,molecules, false, g, highlighted,imageSize);
        if (borderColor != background)
        	paintBorderShadow(g,getBorderWidth(),new Rectangle(imageSize));
        g.dispose();
		return buffer;
    }
    private void paintBorderShadow(Graphics2D g2, int shadowWidth, Shape shape) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        int sw = shadowWidth*2;
        for (int i=sw; i >= 2; i-=2) {
            float pct = (float)(sw - i) / (sw - 1);
            g2.setColor(getMixedColor(borderColor, pct,
                                      background, 1.0f-pct));
            g2.setStroke(new BasicStroke(i));
            g2.draw(shape);
        }
    }
    private static Color getMixedColor(Color c1, float pct1, Color c2, float pct2) {
        float[] clr1 = c1.getComponents(null);
        float[] clr2 = c2.getComponents(null);
        for (int i = 0; i < clr1.length; i++) {
            clr1[i] = (clr1[i] * pct1) + (clr2[i] * pct2);
        }
        return new Color(clr1[0], clr1[1], clr1[2], clr1[3]);
    }
    
    public synchronized static void generate2D(IAtomContainer molecule,boolean generateCoordinates,IMoleculeSet molecules)
    {
        if (molecule != null) {
            if ((molecule ==null) || (molecule.getAtomCount() == 0)) 
                generateCoordinates=false;
            else if (StructureTypeProcessor.has2DCoordinates(molecule)>0)   
               generateCoordinates=false;
            else generateCoordinates = true;
            
            molecules.removeAllAtomContainers();
            if (!generateCoordinates) {
                MFAnalyser mfa = new MFAnalyser(molecule);
                IAtomContainer c = mfa.removeHydrogensPreserveMultiplyBonded();             	
            	molecules.addAtomContainer(c);
            	return;
            }            
            try
            {
            	IMoleculeSet mset =  ConnectivityChecker.partitionIntoMolecules(molecule);
                StructureDiagramGenerator sdg = new StructureDiagramGenerator();
                IMolecule m = null;
                for (int i=0; i < mset.getAtomContainerCount();i++) {
                    MFAnalyser mfa = new MFAnalyser(mset.getAtomContainer(i));
                    IAtomContainer c = mfa.removeHydrogensPreserveMultiplyBonded();                	
                    if (generateCoordinates) {
                        sdg.setMolecule((IMolecule)c,false);
                        m = null;
                        sdg.generateCoordinates(new Vector2d(0,1));
                        m = sdg.getMolecule();
                        
                    } else {
                    	m = (IMolecule)c;//mset.getMolecule(i);
                    }
                    molecules.addMolecule(m);
                }
                mset.removeAllAtomContainers();
            }
            
            catch(Exception exc)
            {
                molecules.removeAllAtomContainers();
            }

            	
        } else 
        	molecules.removeAllAtomContainers();
    }


	
	public synchronized void paint(Renderer2D renderer, 
    		IMoleculeSet molecules,
			boolean explicitH,  
			Graphics2D g,
			IAtomContainer highlighted) {
		paint(renderer, molecules, explicitH, g, highlighted,getImageSize());
	}
	/**
	 * TODO sort molecules, in order to display the largest part first
	 * @param renderer
	 * @param molecules
	 * @param explicitH
	 * @param g
	 * @param highlighted
	 * @param imageSize
	 */
	public synchronized static void paint(Renderer2D renderer, 
    		IMoleculeSet molecules,
			boolean explicitH,  
			Graphics2D g,
			IAtomContainer highlighted,
			Dimension imageSize)	
	{
    	renderer = createRenderer(imageSize,Color.white);
    	Renderer2DModel r2dm = renderer.getRenderer2DModel();
		/*
		Renderer2DModel r2dm = renderer.getRenderer2DModel();
		
        r2dm.setDrawNumbers(false);
        r2dm.setUseAntiAliasing(true);
        r2dm.setShowImplicitHydrogens(true);
        r2dm.setShowAromaticity(true);
        r2dm.setColorAtomsByType(true);
        r2dm.setSelectedPartColor(Color.orange);
        */
        
		if ((molecules != null) && (molecules.getAtomContainerCount()>0)) {
//			g.setBackground(r2dm.getBackColor());

			
			
			int columns = (int)Math.ceil(Math.sqrt(molecules.getAtomContainerCount()));
			int rows = (int)Math.ceil((double)molecules.getAtomContainerCount() / (double)columns);
			
			int w = (int)Math.ceil((double)imageSize.width/(double)columns);
			int h = (int)Math.ceil((double)imageSize.height/(double)rows);
			int row = 0;
			int col = 0;
			Point2d center = new Point2d();
			
			for (int i=0;i<molecules.getAtomContainerCount();i++) {
				
				Rectangle2D r = new Rectangle((int)Math.round(col*w),(int)Math.round((rows-row-1)*h),w,h);
				Dimension d = new Dimension(w,h);
				center.set(r.getCenterX(),r.getCenterY());
				IAtomContainer mol = molecules.getAtomContainer(i);
	            GeometryTools.translateAllPositive(mol,r2dm.getRenderingCoordinates());
	            GeometryTools.scaleMolecule(mol, d, 0.8,r2dm.getRenderingCoordinates());
	            GeometryTools.center(mol, d,r2dm.getRenderingCoordinates());
	            GeometryTools.translate2DCenterTo(mol,center,r2dm.getRenderingCoordinates());

	    		if (highlighted != null) {
	    			r2dm.setSelectedPart(highlighted);
	    			r2dm.setColorAtomsByType(false);
	    		} 
				renderer.paintMolecule(molecules.getAtomContainer(i),g,r);
				
				col++;
				if (col >= columns) { col = 0; row++; }
			}	

		} else {
			g.setBackground(Color.white);
			g.clearRect(0,0,imageSize.width,imageSize.height);
		}
	}
	protected static void printCoordinates(Renderer2DModel model) {
		
		//System.out.println(model.getRenderingCoordinates().values());
	}


                        
    public synchronized BufferedImage getImage(ArrayList<?> list) {
        if (buffer == null)
            buffer = new BufferedImage(imageSize.width, imageSize.height,
				BufferedImage.TYPE_INT_RGB);
        
		Graphics2D g = buffer.createGraphics();
		g.setColor(background);
		g.fillRect(0, 0, imageSize.width, imageSize.height);
		g.setColor(Color.black);
		int y = 32;
		for (int i=0; i < list.size();i++) {
			g.drawString(list.get(i).toString(), 16,y);
			y += 16;
		}	
	
		return buffer;
    }
    

    public synchronized Color getBackground() {
        return background;
    }
    public synchronized void setBackground(Color background) {
        this.background = background;
    }
    public synchronized Dimension getImageSize() {
        return imageSize;
    }
    public synchronized void setImageSize(Dimension imageSize) {
        this.imageSize = imageSize;
        buffer = null;
        r2dm.setBackgroundDimension(imageSize);
    }
	public Image getDefaultImage() {
		return defaultImage;
	}
	public void setDefaultImage(BufferedImage defaultImage) {
		this.defaultImage = defaultImage;
	}
}
