/*
 * Created on 2005-4-9

 * @author Nina Jeliazkova nina@acad.bg
 *
 * Project : ambit
 * Package : ambit.applets
 * Filename: Viewer2D.java
 */
package ambit.applets;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.MFAnalyser;
import org.openscience.cdk.tools.SaturationChecker;

import ambit.ui.data.molecule.Panel2D;


/**
 * A simple applet to show 2D picture of a Molecule
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-7-20
 */
public class Viewer2D extends JApplet implements Runnable, MouseListener, MouseMotionListener {
	protected Panel2D panel = null;
	protected JLabel status = null;
	protected AtomContainer atomcontainer = null;
	protected boolean uniqueSMILES = false;
	protected String smiles= "";

	/**
	 * Constructor
	 * @throws java.awt.HeadlessException
	 */
	public Viewer2D() throws HeadlessException {
		super();
	}

	public static void main(String[] args) {
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		//TODO read smiles as parameter or CML inputstream
		atomcontainer = new Molecule();
		display("");
		invalidate();

	}
	/* (non-Javadoc)
	 * @see java.applet.Applet#start()
	 */
    public void start() {
    	if (atomcontainer == null) {
    		new Thread(this).start();
    	}
    }  
   public void stop() {
   }	
	   /* (non-Javadoc)
	 * @see java.applet.Applet#init()
	 */
	private void createGUI() {
		super.init();
		getContentPane().setLayout(new BorderLayout());
		panel = new Panel2D();
		getContentPane().add(panel,BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(200,200));
		panel.setMinimumSize(new Dimension(200,200));		
		status = new JLabel("");
		status.setToolTipText("status bar");
		getContentPane().add(status,BorderLayout.SOUTH);
		/*
		JButton b = new JButton(new AbstractAction("Draw") {
			public void actionPerformed(ActionEvent arg0) {
				display(smiles);

			}
		});
		getContentPane().add(b,BorderLayout.NORTH);
		*/
	}
	
	public void init() {
	    //Execute a job on the event-dispatching thread:
	    //creating this applet's GUI.
	    try {
	        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
	            public void run() {
	                createGUI();
	            }
	        });
	    } catch (Exception e) {
	        System.err.println("createGUI didn't successfully complete");
	    }
	}

	protected void setMolecule(AtomContainer atomcontainer) {
		try {
			panel.setAtomContainer(atomcontainer,true);
		} catch (Exception e) {
		    e.printStackTrace();
			JOptionPane.showMessageDialog(getParent(),
				    smiles,						
				    "Invalid SMILES",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void display(String newSmiles) {
		updateMolecule(newSmiles);
	    smiles = newSmiles;
		showStatus(smiles);
		try {
		if (smiles.equals("") ||  smiles.equals("NA")) {
			panel.setAtomContainer(null,false);
		} else {
			setMolecule(atomcontainer);
		}
		} catch (Exception ex) {
			panel.setAtomContainer(null,false);
			repaint();
			ex.printStackTrace();

		}	
	}
	public boolean analyzeSubstance(Molecule mol) {
		if (mol == null) return false;
		
		MFAnalyser mfa = new MFAnalyser(mol);
		if (mfa.getAtomCount("H") == 0) {
			
			//TODO to insert H if necessary
			HydrogenAdder adder;
			try {
				SaturationChecker sc = new SaturationChecker();
				adder = new HydrogenAdder(sc);
			} catch (IOException x) {
				x.printStackTrace();
				adder =null;
			} catch (ClassNotFoundException x) {
				x.printStackTrace();
				adder = null;
			}
			
			try {
				adder.addImplicitHydrogensToSatisfyValency(mol);
				
			} catch (CDKException x) {
				x.printStackTrace();
				//TODO exception
				//formula = "defaultError";
			}
			
		} //else formula = mfa.getMolecularFormula();
		//mass = mfa.getMass();

		return true;
	}

	/*
	protected void testCanonicalLabeling(Molecule mol) {
		 //CanonicalLabeler cl = new CanonicalLabeler();
		 //cl.canonLabel(mol);
		if (mol == null) return;
		 Atom[] all = mol.getAtoms();
		    for (int i = 0; i < all.length; i++) {
		        System.out.println(
		        	"Atom : "+ (i+1) +
		        	"\tType :" + all[i].getSymbol() +
					"\tLabel :" +
		        	((Long) all[i].getProperty("CanonicalLable")).toString()
					);
		    }

		 //cl = null;
	}
	*/
	protected String genSmiles() {
		if (!uniqueSMILES) {
			SmilesGenerator gen = new SmilesGenerator(DefaultChemObjectBuilder.getInstance());
			try {
				
				Molecule mol = (Molecule) atomcontainer;
				//analyzeSubstance(mol);
				//testCanonicalLabeling(mol);
				smiles = gen.createSMILES(mol).toString();
				uniqueSMILES = true;
			} catch (Exception x) {
				//TODO exception
				System.err.println(this.smiles);
				x.printStackTrace();
				uniqueSMILES = false;
				return "";
			}
			gen = null;
		} 
		return smiles;
	}
	public boolean updateMolecule(String newSmiles) {
		//create molecule if null
		//if (smiles.equals(oldSmiles)) return true;
	    if (newSmiles.equals("")) {
	        showStatus("Empty SMILES !");
	        return false;
	    }
		uniqueSMILES = false;
		
		try {
		    SmilesParser parser = new SmilesParser();
			atomcontainer = parser.parseSmiles(newSmiles.trim());
			parser = null;
		
		} catch (InvalidSmilesException x) {
			atomcontainer = null;
			x.printStackTrace();
			System.err.println(newSmiles);
			return false;
		}	
			
		//create smiles if not unique
		smiles = genSmiles();
		//smiles = newSmiles.trim();
		
		return true;
	}
	public String getSmiles() {
		if (smiles.equals("")) genSmiles();
		return smiles;
	}
	/**
	 * Regenerates a formula in a right order (C H ???) 
	 * @param formula
	 * @return Normalized formula 
	 * TODO move it out of UI
	 */
	public String normalizeFormula(String formula) {
			if (formula.equals("")) return "";
			try {
			MFAnalyser mfa = new MFAnalyser(formula,new Molecule());
			//this is to make the formula "canonical" (i.e. sorted in the right order)
			mfa = new MFAnalyser(mfa.getAtomContainer());
			formula = mfa.getMolecularFormula();			
			return formula;
			} catch (Exception x) {
				return "";
			}
	}

}
