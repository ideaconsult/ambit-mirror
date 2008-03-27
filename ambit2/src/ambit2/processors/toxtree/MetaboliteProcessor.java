package ambit2.processors.toxtree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IMolecule;

import toxTree.exceptions.ReactionException;
import toxTree.query.MolAnalyser;
import toxTree.query.MolFlags;
import toxTree.query.SimpleReactions;
import ambit2.ui.UITools;
import ambit2.ui.editors.DefaultProcessorEditor;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.exceptions.AmbitException;
import ambit2.processors.DefaultAmbitProcessor;
import ambit2.processors.IAmbitResult;

/**
 * Generates products of few hydrolysis reactions and assigns as a molecule property. Uses {@link toxTree.query.SimpleReactions}.  
 * The products can be accessed by <code>
 *  MolFlags mf = (MolFlags) mol.getProperty(MolFlags.MOLFLAGS);
 *  ISetOfAtomContainers products = mf.getHydrolysisProducts();
 *  </code>
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class MetaboliteProcessor extends DefaultAmbitProcessor {
    protected boolean interactive = true;
    protected Container frame = null;
	public MetaboliteProcessor() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Object process(Object object) throws AmbitException {
		if (object instanceof IMolecule) {
						
			try {
			    IMolecule mol = (IMolecule) ((IMolecule) object).clone();
				MolAnalyser.analyse(mol); 
			    MolFlags mf = (MolFlags) mol.getProperty(MolFlags.MOLFLAGS);
			    if (mf == null) throw new AmbitException("Can't process structure");
			    IAtomContainerSet sc = mf.getHydrolysisProducts();
			    if (sc == null) {
					SimpleReactions sr = new SimpleReactions();
					try {
						sc = sr.isReadilyHydrolised(mol);
						if ((sc==null) || (sc.getAtomContainerCount()==0)) {
							showNoDataMessage();
							mf = null;
						} else {
							
						}
					} catch (ReactionException x) {
						mf = null;
						throw new AmbitException(x);
					}
			    } 
			    if (mf != null) {
					mf.setHydrolysisProducts(sc);
					((Molecule) object).setProperty(MolFlags.MOLFLAGS,mf);
			    }	
			    

			} catch (Exception x) {
				throw new AmbitException(x);
			}
			return object;

		} else return null;

	}

	public IAmbitResult createResult() {
		// TODO Auto-generated method stub
		return null;
	}

	public IAmbitResult getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setResult(IAmbitResult result) {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#close()
     */
    public void close() {

    }
	public void showNoDataMessage() {
	    if (!interactive) return;
		ImageIcon icon  = null;
		try {
			icon = UITools.createImageIcon("ambit2/ui/images/hydrolysis_reactions.png");
		} catch (Exception x) {
			icon =null;
		}
		String m = "<html><b>This DEMO uses only following hydrolysis reactions to generate metabolites.</b></html>";
		JPanel p = new JPanel(new BorderLayout());
		p.add(new JLabel(m),BorderLayout.NORTH);
		JLabel reactions = new JLabel("",icon,JLabel.CENTER);
		reactions.setBorder(BorderFactory.createLineBorder(Color.blue));
		reactions.setToolTipText("Click here to go to AMBIT site");
		reactions.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				super.mouseClicked(arg0);
				UITools.openURL("http://ambit2.acad.bg");
			}
		});        		
		p.add(reactions,BorderLayout.CENTER);
		p.add(new JLabel("<html><b>Use <u>Molecule/Metabolites/Search</u> to find compounds that could be metabolized \nand then run <u>Molecule/Metabolites</u></b></html> "),BorderLayout.SOUTH);
		JOptionPane.showMessageDialog(frame,p,"No metabolites generated",JOptionPane.PLAIN_MESSAGE);

	}

    public synchronized boolean isInteractive() {
        return interactive;
    }
    public synchronized void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

	public Container getFrame() {
		return frame;
	}

	public void setFrame(Container frame) {
		this.frame = frame;
	}
	   public IAmbitEditor getEditor() {

	    	return new DefaultProcessorEditor(this);
	    }
	public String toString() {
		return "Generates hydrolysis products";
	}   
}
