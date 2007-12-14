package ambit.ui.actions.process;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.vecmath.Vector2d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.applications.jchempaint.JCPPropertyHandler;
import org.openscience.cdk.applications.jchempaint.JChemPaintEditorPanel;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.MFAnalyser;

import ambit.data.ISharedData;
import ambit.data.molecule.MoleculesIterator;
import ambit.database.data.ISharedDbData;
import ambit.misc.AmbitCONSTANTS;
import ambit.ui.UITools;
import ambit.ui.actions.AbstractMoleculeAction;


/**
 * 
 * Launches JChemPaint structure diagram editor for a preset molecule
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-10-23
 */
public class MoleculeEditAction extends AbstractMoleculeAction {
	protected ISetOfMolecules molecules;
	protected JChemPaintModel jcpModel;
	protected StructureDiagramGenerator sdg = null;
	protected boolean query = false;
	protected int value = -1;
	/**
	 * 
	 */
	private static final long serialVersionUID = 5166718649430988452L;

	public MoleculeEditAction(IMolecule molecule,Object userData, JFrame mainFrame) {
		this(molecule,userData,mainFrame,"Edit");
	}

	public MoleculeEditAction(IMolecule molecule, Object userData, JFrame mainFrame, String arg0) {
		this(molecule,userData,mainFrame, arg0,UITools.createImageIcon("ambit/ui/images/edit.png"));
	}

	public MoleculeEditAction(IMolecule molecule,Object userData, JFrame mainFrame, String arg0, Icon arg1) {
		super(molecule,userData,mainFrame, arg0, arg1);
		setJCPModel();

	}
	protected void setJCPModel() {
	    
		jcpModel = new JChemPaintModel();        
		jcpModel.setTitle("JChemPaint structure diagram editor");
		jcpModel.setAuthor(JCPPropertyHandler.getInstance().getJCPProperties().getProperty("General.UserName"));
		Package jcpPackage = Package.getPackage("org.openscience.cdk.applications.jchempaint");
		String version = jcpPackage.getImplementationVersion();
		jcpModel.setSoftware("JChemPaint " + version);
		jcpModel.setGendate((Calendar.getInstance()).getTime().toString());		
	}

	public void actionPerformed(ActionEvent arg0) {
		run(arg0);
		done();
	}
	
	public void run(ActionEvent arg0) {
		super.run(arg0);
		if (userData instanceof ISharedData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);
		
			IMolecule mole = null;
			if (query) mole = dbaData.getQuery();
			else mole = dbaData.getMolecule();
			IMolecule molecule = editMolecule(mole);
	        if (query) dbaData.setQuery(molecule);
	        else dbaData.setMolecule(molecule);
		} else if (userData instanceof MoleculesIterator) {
			IMolecule mole = (IMolecule) ((MoleculesIterator) userData).getAtomContainer();
			IMolecule molecule = editMolecule(mole);
			((MoleculesIterator) userData).set(molecule);
		} else JOptionPane.showMessageDialog(mainFrame, "This dataset is read only! Can't modify molecule!");
			
	}
	public IMolecule editMolecule(IMolecule mole) {		
		//if (userData instanceof ISharedData) {
			/*
		    ISharedDbData dbaData = ((ISharedDbData) userData);
		
			IMolecule mole = null;
			if (query) mole = dbaData.getQuery();
			else mole = dbaData.getMolecule();
			*/
			if (mole == null) {
		    	mole = DefaultChemObjectBuilder.getInstance().newMolecule();
		    	mole.addAtom(new Atom("C"));
		    	mole.setProperty("SMILES","C");
			}
			setMolecule(mole);
		
	    	if (molecules != null) {
				jcpModel.getChemModel().setSetOfMolecules(molecules);
				
				Dimension d = new Dimension(460,450);
	    		JChemPaintEditorPanel jcpep = new JChemPaintEditorPanel(2,d,true,"stable");
	    		jcpep.setPreferredSize(d);
	    		jcpep.registerModel(jcpModel);
	    		jcpep.setJChemPaintModel(jcpModel);
	    		
	    		//JFrame pane = getJCPFrame(jcpep);
	    		
	    		
	    		JOptionPane pane = new JOptionPane(jcpep, JOptionPane.PLAIN_MESSAGE,JOptionPane.OK_CANCEL_OPTION,null);    		
	    		JDialog dialog = pane.createDialog(mainFrame, "JChemPaint Structure diagram editor");
	    		dialog.setBounds(300,100,d.width+100,d.height+100);
	    		dialog.setVisible(true);
	    		
	    		if (pane.getValue() == null) return mole;
	    		
	    		//super.run(arg0);
	    		pane.setVisible(true);
	    		
	    		int value = ((Integer) pane.getValue()).intValue();
	    		//while (value != 0);
	    		if (value == 0) { //ok
	    	    	molecules = jcpep.getJChemPaintModel().getChemModel().getSetOfMolecules();
	    	    	if (molecule == null)  molecule = new org.openscience.cdk.Molecule(); 
	    	    	else 	molecule.removeAllElements();
	    	        for (int i=0; i < molecules.getAtomContainerCount(); i++) 
	    	            molecule.add(molecules.getMolecule(i));
	    	        
	    	        
	    	        //((Compound)dbaData.getQueryObject()).setMolecule(molecule);
	    	        if (JOptionPane.showConfirmDialog(mainFrame, "Remove all properties of the molecule?\n(Yes - to remove, No - to keep)", 
	    	        			"Structure diagram editor", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
	    	        	molecule.getProperties().clear();
					IMolecule m = null;
					try {
					    m = (IMolecule )molecule.clone();
					} catch (Exception x) {
					    m = molecule;
					}
					MFAnalyser mfa = new MFAnalyser(m);
					m = (Molecule)mfa.removeHydrogensPreserveMultiplyBonded();
     
	    	        SmilesGenerator g = new SmilesGenerator(DefaultChemObjectBuilder.getInstance());
	    	        molecule.setProperty(AmbitCONSTANTS.SMILES,g.createSMILES(m));
	    	        m = null;
	    	        mfa = null;
	    	        g = null;
	    	        return molecule;
	    	        /*
	    	        if (query) dbaData.setQuery(molecule);
	    	        else dbaData.setMolecule(molecule);
    	        
	    	        return;
	    	        */
	    	    	
	    		} else setMolecule(null);
	    	}
		//}
	    	return mole;
		
	}
	
	public void setMolecule(IMolecule molecule) {
		super.setMolecule(molecule);
		try {
			molecules = getMoleculeForEdit(molecule);
		} catch (Exception x) {
			x.printStackTrace();
			molecules = null;
		}
	}

	protected ISetOfMolecules getMoleculeForEdit(IAtomContainer atomContainer) throws Exception {
		if (atomContainer == null) return null;
		if (atomContainer instanceof QueryAtomContainer) {
			return null;
		}
		
		IMolecule[] molecules = ConnectivityChecker.partitionIntoMolecules(atomContainer).getMolecules();
		if (molecules.length == 0) {
			molecules = new IMolecule[1];
		 	IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
	    	mol.addAtom(new Atom("C"));
	    	mol.setProperty("SMILES","C");
			molecules[0] = mol;
		}
		
		SetOfMolecules m =  new org.openscience.cdk.SetOfMolecules();
		
		for (int i=0; i< molecules.length;i++) {
			IMolecule a = molecules[i];
			if (!GeometryTools.has2DCoordinates(a)) {
				if (sdg == null) sdg = new StructureDiagramGenerator();
				sdg.setMolecule((Molecule)a);
				sdg.generateCoordinates(new Vector2d(0,1));
				molecules[i] = sdg.getMolecule();
			}
			m.addMolecule(molecules[i]);
		}
		return m;		
	}

	

    public synchronized boolean isQuery() {
        return query;
    }
    public synchronized void setQuery(boolean query) {
        this.query = query;
    }
    public JFrame getJCPFrame(JChemPaintEditorPanel jcpep) {
		JFrame frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
		    /* (non-Javadoc)
             * @see java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
             */
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                value = 0;
            }
		});
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(jcpep);
		frame.setTitle(jcpModel.getTitle());
		frame.setBounds(100,100,400,400);
		return frame;
    }
}



