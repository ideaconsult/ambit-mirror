/*
 * Created on 2006-3-5
 *
 */
package ambit.ui.data;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.ISetOfAtomContainers;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.Renderer2D;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.MFAnalyser;

import ambit.data.molecule.AmbitPoint;
import ambit.data.molecule.Compound;
import ambit.data.molecule.ListOfAtomContainers;

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
        if (o instanceof Compound)
            return getImage((Compound)o);
        else if (o instanceof AmbitPoint) {
            return getImage(((AmbitPoint)o).getCompound());
        } else if (o instanceof Molecule)
            return getImage((IAtomContainer)o);
        else return defaultImage;
    }
    public BufferedImage getImage(Compound o) {
        return getImage(o.getMolecule());
    }
    public synchronized BufferedImage getImage(String smiles) {
        ArrayList<String> m = null;
        try {
            if (parser == null) parser = new SmilesParser();
            return getImage(parser.parseSmiles(smiles));
        } catch (Exception x) {
            m = new ArrayList<String>();
            m.add(x.getMessage());
        }
        return getImage(m);
    }
    public synchronized BufferedImage getImage(IAtomContainer molecule) {    
        
        if (buffer == null)
            buffer = new BufferedImage(imageSize.width, imageSize.height,
				BufferedImage.TYPE_INT_RGB);
        
		Graphics2D g = buffer.createGraphics();
		g.setColor(background);
		g.fillRect(0, 0, imageSize.width, imageSize.height);

        ListOfAtomContainers molecules = new ListOfAtomContainers();
        generate2D(molecule, true, molecules);
        paint(renderer,molecules, false, g,null,null);

		return buffer;
    }
    public synchronized static void generate2D(IAtomContainer molecule,boolean generateCoordinates,ListOfAtomContainers molecules)
    {
        if (molecule != null) {
            if ((molecule ==null) || (molecule.getAtomCount() == 0)) 
                generateCoordinates=false;
            //else if (GeometryTools.has2DCoordinates(molecule))   
              //  generateCoordinates=false;
            //System.out.println("panel 2D\t"+Boolean.toString(generateCoordinates));
            
            ISetOfAtomContainers c =  ConnectivityChecker.partitionIntoMolecules(molecule);     
            try
            {
                StructureDiagramGenerator sdg = new StructureDiagramGenerator();
                molecules.clear();
                IMolecule m = null;
                for (int i=0; i < c.getAtomContainerCount();i++) { 
                    if (generateCoordinates) {
                        sdg.setMolecule((Molecule)c.getAtomContainer(i));
                        m = null;
                        sdg.generateCoordinates(new Vector2d(0,1));
                        m = sdg.getMolecule();
                    } else m = (Molecule)c.getAtomContainer(i);
                    molecules.add(m);
                }
            }
            catch(Exception exc)
            {
                molecules.clear();
            }
        } else molecules.clear();     
    }
    public synchronized static void paint(Renderer2D renderer, 
    			ListOfAtomContainers molecules,
    			boolean explicitH,  
    			Graphics g,
    			int[] highlightedAtoms,
    			int[] highlightedBonds)
    {
        Renderer2DModel r2dm = renderer.getRenderer2DModel();
        if (molecules != null) {
            
            Dimension d = r2dm.getBackgroundDimension();
            g.setColor(Color.white);
            g.fillRect(0,0,d.width,d.height);
            Point2d dd[] = new Point2d[molecules.size()];
            
            switch (molecules.size()) {
            case 1: { dd[0] = new Point2d(0,0); 
                GeometryTools.translateAllPositive((IMolecule)molecules.get(0)); break;}
            case 2: {
                dd[0] = new Point2d(d.width/4,d.height/2);
                dd[1] = new Point2d(3*d.width/4,d.height/2);
                break;
            }
            case 3: {
                dd[2] = new Point2d(d.width/4,d.height/4);
                dd[1] = new Point2d(3*d.width/4,d.height/4);
                dd[0] = new Point2d(d.width/4,3*d.height/4);                
                break;
            }
            case 4: {
                dd[3] = new Point2d(d.width/4,d.height/4);
                dd[2] = new Point2d(3*d.width/4,d.height/4);
                dd[1] = new Point2d(d.width/4,3*d.height/4);                
                dd[0] = new Point2d(3*d.width/4,3*d.height/4);              
                break;
            } 
            default : {
                for (int i=0;i<dd.length;i++) dd[i] = new Point2d(d.width/2,d.height/2);
                break;
            }
            }
            if (molecules.size() >= 3) 
                d = new Dimension(d.width/2,d.height/2);
            else if (molecules.size() >= 2) 
                d = new Dimension(d.width/2,d.height);
            try {
                for (int i=0;i<molecules.size();i++) {
                    IAtomContainer c = molecules.getAtomContainer(i);
                    if (explicitH) {
                        
                    } else {
                        MFAnalyser mfa = new MFAnalyser((AtomContainer)c.clone());
                        c = mfa.removeHydrogensPreserveMultiplyBonded();
                    }
                    //GeometryTools.translateAllPositive(c);
                    //
                    GeometryTools.scaleMolecule(c, d, 0.8);
                    GeometryTools.center(c, d);
                    if (molecules.size() > 1) {
                        GeometryTools.translateAllPositive(c);
                        GeometryTools.translate2DCentreOfMassTo(c,dd[i]);
                        
                    }
                    
                    if (highlightedAtoms != null) {
                    	Hashtable ht = r2dm.getColorHash();
                    	ht.clear();
                    	r2dm.setKekuleStructure(true);
                    	for (int a=0; a < highlightedAtoms.length; a++)
                    		try {
                    		ht.put(c.getAtomAt(highlightedAtoms[a]), Color.blue);
                   		
                    		} catch (Exception x) {
                    			//just in case there is no such atom
                    			
                    		}
                    } //else r2dm.setKekuleStructure(false);
                    
                    if (highlightedBonds != null) {
                    	
                    	Hashtable ht = r2dm.getColorHash();
                    	ht.clear();
                    	for (int a=0; a < highlightedBonds.length; a++)
                    		try {
                    		ht.put(c.getBondAt(highlightedBonds[a]), Color.blue);
                    		} catch (Exception x) {
                    			//just in case there is no such atom
                    		}                    		
                    }                  
 
                   
                    renderer.paintMolecule(c, (Graphics2D)g,false,true);
                }
            } catch (Exception x) {
                x.printStackTrace();
                g.clearRect(0,0,r2dm.getBackgroundDimension().width,r2dm.getBackgroundDimension().height);
            }
            
        } else {
            g.clearRect(0,0,r2dm.getBackgroundDimension().width,r2dm.getBackgroundDimension().height);
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
        buffer = new BufferedImage(imageSize.width, imageSize.height,
				BufferedImage.TYPE_INT_RGB);
        this.imageSize = imageSize;
        r2dm.setBackgroundDimension(imageSize);
    }
	public Image getDefaultImage() {
		return defaultImage;
	}
	public void setDefaultImage(BufferedImage defaultImage) {
		this.defaultImage = defaultImage;
	}
	public static BufferedImage emptyImage(int width, int height, Color background, String message) {
		BufferedImage buffer = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics g = buffer.createGraphics();
        g.setColor(background);
        g.fillRect(0, 0, width, height);
        g.drawString(message, 10,10);
        return buffer;
	}	
}
