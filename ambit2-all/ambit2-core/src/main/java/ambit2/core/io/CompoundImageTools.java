/*
 * Created on 2006-3-5
 *
 */
package ambit2.core.io;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.Renderer2D;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.smiles.SmilesParser;


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
    protected SmilesParser parser = null;
    /**
     * 
     */
    public CompoundImageTools() {
        this(new Dimension(200,200));
    }
    public CompoundImageTools(Dimension cellSize) {
        super();
		r2dm = new Renderer2DModel();
		renderer = new Renderer2D(r2dm);
		r2dm.setBackgroundDimension(cellSize);
		r2dm.setBackColor(background);
		r2dm.setForeColor(Color.BLACK);
		r2dm.setDrawNumbers(false);
		r2dm.setUseAntiAliasing(true);
		r2dm.setColorAtomsByType(true);
		r2dm.setShowImplicitHydrogens(false);
		r2dm.setShowAromaticity(true);
		this.imageSize = cellSize;
    }
    public synchronized BufferedImage getImage(Object o) {
        if (o instanceof IAtomContainer)
            return getImage((IAtomContainer)o);
        else return defaultImage;
    }

    public synchronized BufferedImage getImage(String smiles) {
        ArrayList<String> m = null;
        try {
            if (parser == null) parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
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
        g.dispose();
		return buffer;
    }
    public synchronized static void generate2D(IAtomContainer molecule,boolean generateCoordinates,IMoleculeSet molecules)
    {
        if (molecule != null) {
            if ((molecule ==null) || (molecule.getAtomCount() == 0)) 
                generateCoordinates=false;
            else if (GeometryTools.has2DCoordinates(molecule))   
               generateCoordinates=false;
            else generateCoordinates = true;
            
            molecules.removeAllAtomContainers();
            
            try
            {
            	IMoleculeSet mset =  ConnectivityChecker.partitionIntoMolecules(molecule);
                StructureDiagramGenerator sdg = new StructureDiagramGenerator();
                IMolecule m = null;
                for (int i=0; i < mset.getAtomContainerCount();i++) { 
                    if (generateCoordinates) {
                        sdg.setMolecule((IMolecule)mset.getAtomContainer(i),false);
                        m = null;
                        sdg.generateCoordinates(new Vector2d(0,1));
                        m = sdg.getMolecule();
                        
                    } else m = mset.getMolecule(i);
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
	public synchronized static void paint(Renderer2D renderer, 
    		IMoleculeSet molecules,
			boolean explicitH,  
			Graphics2D g,
			IAtomContainer highlighted,
			Dimension imageSize)	
	{
		Renderer2DModel r2dm = renderer.getRenderer2DModel();

        r2dm.setDrawNumbers(false);
        r2dm.setUseAntiAliasing(true);
        r2dm.setShowImplicitHydrogens(true);
        r2dm.setShowAromaticity(true);
        r2dm.setColorAtomsByType(true);
        r2dm.setSelectedPartColor(Color.orange);
        
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
	            GeometryTools.translate2DCentreOfMassTo(mol,center,r2dm.getRenderingCoordinates());

	    		if (highlighted != null) {
	    			r2dm.setSelectedPart(highlighted);
	    			r2dm.setColorAtomsByType(false);
	    		} 
				renderer.paintMolecule(molecules.getAtomContainer(i),g,r);
				
				col++;
				if (col >= columns) { col = 0; row++; }
			}	

		} else {
			g.clearRect(0,0,imageSize.width,imageSize.height);
		}
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
