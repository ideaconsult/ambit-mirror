package ambit2.ui.query;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.vecmath.Vector2d;

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.jchempaint.JCPPropertyHandler;
import org.openscience.cdk.applications.jchempaint.JChemPaintEditorPanel;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.MFAnalyser;

import ambit2.config.AmbitCONSTANTS;
import ambit2.data.molecule.DataContainer;
import ambit2.ui.AmbitColors;
import ambit2.ui.actions.AmbitAction;
/**
 * This panel contains {@link org.openscience.cdk.applications.jchempaint.JChemPaintEditorPanel}
 * and allows the user to edit structure query. See example for {@link ambit2.database.data.AmbitDatabaseToolsData}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class StructureQueryPanel extends JPanel implements Observer {
	protected IMoleculeSet molecules;
	protected JChemPaintModel jcpModel;
	protected StructureDiagramGenerator sdg = null;
	protected int value = -1;
	protected DataContainer dataContainer = null;
	protected JChemPaintEditorPanel jcpep;
	
	public StructureQueryPanel(DataContainer data,ActionMap actions) {
		super();
		
		addWidgets(data,actions);
	}

	public void addWidgets(DataContainer data,ActionMap actions) {
		this.dataContainer = data;
		
		data.addObserver(this);
		createJCPModel();
		setMolecule();

		setLayout(new BorderLayout());
		add(jcpep,BorderLayout.CENTER);
		jcpep.setBorder(BorderFactory.createLineBorder(AmbitColors.DarkClr));
		jcpep.setMinimumSize(new Dimension(200,200));
		
		
		JPanel toolbar = new JPanel();
		toolbar.setMinimumSize(new Dimension(Integer.MAX_VALUE,64));
		setBackground(AmbitColors.BrightClr);
		setForeground(AmbitColors.DarkClr);
		setBorder(BorderFactory.createTitledBorder("Search by structure"));
        if (actions != null) {
        	toolbar.setLayout(new GridLayout(1,actions.size()));
            Object[] keys = actions.allKeys();
            if (keys != null) {
    	        for (int i=0; i < keys.length;i++) {
    	        	Action a = new QueryAction((AmbitAction)actions.get(keys[i]),this);
    	        	JButton b = new JButton(a);
    	        	b.setMinimumSize(new Dimension(32,24));
    	        	b.setPreferredSize(new Dimension(48,32));
    	        	toolbar.add(b);
    	        }	
            }
            }

		add(toolbar,BorderLayout.SOUTH);
	}
	protected void createJCPModel() {
		jcpModel = new JChemPaintModel();        
		jcpModel.setTitle("JChemPaint structure diagram editor");
		jcpModel.setAuthor(JCPPropertyHandler.getInstance().getJCPProperties().getProperty("General.UserName"));
		Package jcpPackage = Package.getPackage("org.openscience.cdk.applications.jchempaint");
		String version = jcpPackage.getImplementationVersion();
		jcpModel.setSoftware("JChemPaint " + version);
		jcpModel.setGendate((Calendar.getInstance()).getTime().toString());
		Dimension d = new Dimension(500,500);
		jcpep = new JChemPaintEditorPanel(2,d,true,"stable");
		jcpep.setEmbedded();
		jcpep.setPreferredSize(d);
		jcpep.registerModel(jcpModel);
		jcpep.setJChemPaintModel(jcpModel,new Dimension(200,200));
		
		
	}
	public void setMolecule() {
		try {
		    ChemModel chemModel = (ChemModel)jcpModel.getChemModel();
		    IMoleculeSet m = chemModel.getMoleculeSet();
		    if (m != null) m.removeListener(chemModel);
		    
			IAtomContainer mole = dataContainer.getMolecule();
			if (mole == null) {
		    	mole = new org.openscience.cdk.Molecule();
		    	//mole.addAtom(new Atom("C"));
		    	//mole.setProperty("SMILES","C");
			}			
			molecules = getMoleculeForEdit(mole);
			chemModel.setMoleculeSet(molecules);
            // to ensure, that the molecule is  shown in the actual visibile part of jcp
            jcpep.scaleAndCenterMolecule((ChemModel)jcpep.getChemModel());
//         fire a change so that the view gets updated
            jcpModel.fireChange(jcpep.getChemModel());
		} catch (Exception x) {
			x.printStackTrace();
			molecules = null;
		}
	}

	protected IMoleculeSet getMoleculeForEdit(IAtomContainer atomContainer) throws Exception {
		if (atomContainer == null) return null;
		if (atomContainer instanceof QueryAtomContainer) {
			return null;
		}
		
		Iterator<IAtomContainer> molecules = ConnectivityChecker.partitionIntoMolecules(atomContainer).molecules();
		/*

		*/
		
		IMoleculeSet m =  DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		
		while (molecules.hasNext()) {
			IAtomContainer a = molecules.next();
			if ((a.getAtomCount() > 0) && !GeometryTools.has2DCoordinates(a)) {
				if (sdg == null) sdg = new StructureDiagramGenerator();
				sdg.setMolecule((Molecule)a);
				sdg.generateCoordinates(new Vector2d(0,1));
				a = sdg.getMolecule();
			}
			m.addMolecule((IMolecule)a);
		}
		if (m.getMoleculeCount() == 0) {
			m.addMolecule(DefaultChemObjectBuilder.getInstance().newMolecule());
		}		
		return m;		
	}
	public void updateMolecule() {
    	molecules = jcpep.getJChemPaintModel().getChemModel().getMoleculeSet();
    	if (molecules == null) molecules = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
    	Molecule  molecule = new org.openscience.cdk.Molecule(); 
        for (int i=0; i < molecules.getAtomContainerCount(); i++) 
            molecule.add(molecules.getMolecule(i));
        
        //((Compound)dbaData.getQueryObject()).setMolecule(molecule);
        molecule.getProperties().clear();
		IMolecule m = null;
		try {
		    m=(Molecule)molecule.clone();
		} catch (Exception x) {
		    x.printStackTrace();
		}
		MFAnalyser mfa = new MFAnalyser(m);
		m = (Molecule)mfa.removeHydrogensPreserveMultiplyBonded();

        SmilesGenerator g = new SmilesGenerator();
        molecule.setProperty(AmbitCONSTANTS.SMILES,g.createSMILES(m));
        m = null;
        mfa = null;
        g = null;
        
        dataContainer.setMolecule(molecule);
    
        return;
    	
	}
	public void update(Observable arg0, Object arg1) {
		setMolecule();
		if ((dataContainer.getMolecule() != null) && (dataContainer.getMolecule().getAtomCount()>0)) {
		    jcpModel.fireChange(jcpep.getChemModel());
		    /*
			CleanupAction a = new CleanupAction();
			a.setJChemPaintPanel(jcpep);
	
			a.actionPerformed(null);
			a = null;
			*/
		}
		
		/*
		jcpep.getJChemPaintModel().getChemModel().setSetOfMolecules(ConnectivityChecker.partitionIntoMolecules(
				((DataContainer)arg0).getMolecule()
				));
		*/
		
		
	}
}

class QueryAction extends AmbitAction {
	protected AmbitAction action;
	protected StructureQueryPanel queryPanel;
	
	public QueryAction(AmbitAction action, StructureQueryPanel queryPanel) {
		super(action.getUserData(),action.getMainFrame(),
				action.getValue(AbstractAction.NAME).toString());
		putValue(AbstractAction.SHORT_DESCRIPTION, action.getValue(AbstractAction.SHORT_DESCRIPTION).toString());
		this.action = action;
		this.queryPanel = queryPanel;
		setActions(action.getActions());
		if (action.getActions() != null)
		action.getActions().put("QUERY"+action.getValue(AbstractAction.NAME).toString(), this);

	}
	
	
	public void run(ActionEvent arg0) {
		try {
			queryPanel.updateMolecule();
			action.run(arg0);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	
	/*
	public void actionPerformed(ActionEvent arg0) {
		try {
			setEnabled(false);
			queryPanel.updateMolecule();
			action.actionPerformed(arg0);
			setEnabled(true);
		} catch (Exception x) {
			
		}
		
	}
	*/
}
