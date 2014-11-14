package ambit2.ui.jmol;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolViewer;
import org.jmol.popup.JmolPopup;
import org.jmol.viewer.Viewer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMoleculeSet;

/**
 * JMol wrapper, used in {@link Panel3D}
 * @author nina
 *
 */
public class ImageTools3D extends DefaultAmbitProcessor<IAtomContainer, BufferedImage> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Dimension currentSize;
	JmolViewer viewer;
	JmolPopup jmolpopup;
	JmolAdapter adapter;
	IMoleculeSet moleculeSet;
    IChemModel model;
    IChemSequence sequence;
    IChemFile chemFile;
       
    public ImageTools3D(Component component,Dimension size) throws Exception {
    	 super();
    	 currentSize = size;
   	     adapter = new CdkJmolAdapter(null);
   	     viewer = Viewer.allocateViewer(component, adapter);
   	     jmolpopup = JmolPopup.newJmolPopup(viewer,true);
   	    viewer.setJmolStatusListener(new StatusListener(jmolpopup));
   	     
   	     moleculeSet = new MoleculeSet();
   	     model = new ChemModel();
   	     model.setMoleculeSet(moleculeSet);
   	     sequence = new ChemSequence();
   	     sequence.addChemModel(model);
   	     chemFile = new ChemFile();
   	     chemFile.addChemSequence(sequence);
   	     
	}
	public JmolViewer getViewer() {
		return viewer;
	}    
	public BufferedImage process(IAtomContainer target) throws AmbitException {
		setObject(target);
		BufferedImage scaledImage = new BufferedImage(currentSize.width,currentSize.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = scaledImage.createGraphics();
		//graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		paint(graphics2D);
		//graphics2D.dispose();		
		return scaledImage;
	}
	public void paint(Graphics g) {
           if (viewer != null) {
    	     viewer.setScreenDimension(currentSize);
    	     Rectangle rectClip = new Rectangle();
    	     g.getClipBounds(rectClip);
    	     g.clearRect(rectClip.x, rectClip.y, rectClip.width,  rectClip.height);
    	     viewer.renderScreenImage(g, currentSize, rectClip);
    	     viewer.script("autobond=false");
//    	     viewer.script("boundbox;axes;wireframe 0.01; spacefill 0;");
           } else {
             Rectangle rectClip = new Rectangle();
             g.getClipBounds(rectClip);
             g.clearRect(rectClip.x, rectClip.y, rectClip.width,  rectClip.height);
           }
	}
	
	   public void setObject(IAtomContainer mol) {
			 if (moleculeSet == null) return;  
			 moleculeSet.removeAllAtomContainers();
			 if (mol != null) moleculeSet.addAtomContainer(mol);
			 try {
				 viewer.openClientFile("","",chemFile);
				 jmolpopup.updateComputedMenus();

			 } catch (Exception x) {
				// x.printStackTrace();
			 }
	   }	
	
	public void executeCmd(String command) {
	    viewer.evalString(command);
	}
	
}