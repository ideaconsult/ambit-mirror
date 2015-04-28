package ambit2.tautomers.test;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.FileReader;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.core.io.MyIteratingMDLReader;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.HydrogenAdderProcessor;
import ambit2.smarts.ChemObjectToSmiles;
import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SMIRKSReaction;
import ambit2.smarts.SmartsConst;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsManager;
import ambit2.smarts.SmartsParser;
import ambit2.smarts.SmartsToChemObject;
import ambit2.ui.Panel2D;


public class TestStrVisualizer 
{
	
	static SmartsParser sp = new SmartsParser();
	//static SmilesParser smilesparser = new SmilesParser(SilentChemObjectBuilder.getInstance());
	static SmartsManager man = new SmartsManager(SilentChemObjectBuilder.getInstance());
	static IsomorphismTester isoTester = new IsomorphismTester();
	static SmartsToChemObject smToChemObj = new SmartsToChemObject(SilentChemObjectBuilder.getInstance());
	static ChemObjectToSmiles cots = new ChemObjectToSmiles(); 
	
	boolean filterEqMaps = true;
	boolean FlagSingleCopyForPos = false;
	JFrame frame;
	JScrollPane scrPane;
	JPanel panel;
	int nStr = 0;
	int nCol = 6;	
	int size = 150;
	String frameTitle = "Structure Visualizer";
	
	public static void main(String[] args) throws Exception 
	{
		//Vector<IAtomContainer> structs = new Vector<IAtomContainer> ();
		//structs.add(MoleculeFactory.make123Triazole());
		//structs.add(MoleculeFactory.makeAdenine());
		
		TestStrVisualizer tsv = new TestStrVisualizer(); 
		//tsv.testSMIRKS2("[N:1][C:2]([H])>>[N:1][H].[C:2]=[O]", "c1cc(OCCN(C([H])([H])S)(C([H])Cl))ccc1C(c1ccc(OCNC[H])cc1)=C(CC)c1ccccc1");
		//tsv.testSMIRKS2("[N:1][C:2]([H])>>[N:1][H].[C:2]=[O]", "c1cc(OCCN(C([H])([H])S)(C([H])([H])Cl))ccc1CC([H])NC[H]");
		
		//tsv.testSMIRKS("[#7:1][#6:2]>>[#7+:1]([O-])[#6:2]","CCN");
		//tsv.testSMIRKS("[#7:1][#6:2]>>[#7+:1]([O-])[#6:2]","CCN", false);		
		//tsv.testSMIRKS("[c:1][H:2]>>[c:1][O][H:2]","n1ccccc1", true);
		
		
		tsv.testSMIRKS("[N;X3:1]1([H])[#6:2]=[#6:3][#6;X4:4]([H])[#6:5]=[#6:6]1>>[n;H0:1]1=[#6:2][#6:3]=[#6:4][#6:5]=[#6:6]1",
				"N1C=CCC=C1", true);
		
		
		//tsv.testSMIRKS("[N:1][C:2]([H])>>[N:1][H].[C:2]=[O]", 
		//		tsv.getMDLStruct("D:/Projects/nina/test-smirks-structs/4_hydroxytamoxifen.sdf",1));
		
	}
	
	TestStrVisualizer()
	{	
	}
	
	TestStrVisualizer(Vector<IAtomContainer> structs)
	{	
		panel = new JPanel();
		panel.setLayout(new GridLayout(0,nCol));
		addStructures(structs);
		//addTestPanel();addTestPanel();
		setFrame();
	}
	
	TestStrVisualizer(Vector<IAtomContainer> structs, String title)
	{
		frameTitle = title;
		panel = new JPanel();
		panel.setLayout(new GridLayout(0,nCol));
		addStructures(structs);
		setFrame();
		
	}
	
	TestStrVisualizer(List<IAtomContainer> structs)
	{
		panel = new JPanel();
		panel.setLayout(new GridLayout(0,nCol));
		addStructures(structs);
		setFrame();
	}
	
	TestStrVisualizer(List<IAtomContainer> structs, String title)
	{
		frameTitle = title;
		panel = new JPanel();
		panel.setLayout(new GridLayout(0,nCol));
		addStructures(structs);
		setFrame();
	}
	
	void addStructures(Vector<IAtomContainer> structs)
	{
		for (int i = 0; i < structs.size(); i++)
			addStructure(structs.get(i));
	}
	
	void addStructures(List<IAtomContainer> structs)
	{
		for (int i = 0; i < structs.size(); i++)
			addStructure(structs.get(i));
	}
	
	
	
	void setFrame()
	{	
		frame = new JFrame();
		int v=ScrollPaneConstants. VERTICAL_SCROLLBAR_AS_NEEDED;
		int h=ScrollPaneConstants. HORIZONTAL_SCROLLBAR_AS_NEEDED;		
		scrPane = new JScrollPane(panel,v,h);
		frame.getContentPane().add(scrPane);
		
		frame.setTitle(frameTitle);
		frame.setSize(1000, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		frame.setVisible(true);
	}
	
	void addStructure(IAtomContainer struct)
	{	
		Panel2D p = new Panel2D();
		p.setAtomContainer(struct);
		p.setBorder(BorderFactory.createLineBorder(Color.black));
		
		//frame.getContentPane().add(p);
		panel.add(p);
		//p.setBounds((nStr%nCol)*size,(nStr/nCol)*size,size-2,size-2);
		nStr++;
	}
	
	void addTestPanel()
	{	
		JPanel p = new JPanel();
		Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		p.setBorder(raisedetched);
		panel.add(p);
	}
	
	
	public void testSMIRKS(String smirks, String targetSmiles, boolean hAdd) throws Exception
	{
		AtomConfigurator  cfg = new AtomConfigurator();
		HydrogenAdderProcessor hadder = new HydrogenAdderProcessor();
		
		System.out.println("Testing SMIRKS: " + smirks);
		SMIRKSManager smrkMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
		smrkMan.setFlagFilterEquivalentMappings(filterEqMaps);
		smrkMan.setSSMode(SmartsConst.SSM_NON_IDENTICAL);
		
		SMIRKSReaction reaction = smrkMan.parse(smirks);
		if (!smrkMan.getErrors().equals(""))
		{
			System.out.println(smrkMan.getErrors());
			return;
		}
		
		System.out.println(reaction.transformationDataToString());
		
		if (targetSmiles.equals(""))
			return;
		
		setFrame();
		
		IAtomContainer target = SmartsHelper.getMoleculeFromSmiles(targetSmiles);
		
		if (hAdd)
			hadder.process(target);
		cfg.process(target);
		CDKHueckelAromaticityDetector.detectAromaticity(target);
		
		System.out.println("reactant:");
		for (int i = 0; i < target.getAtomCount(); i++)
			System.out.println(target.getAtom(i).getSymbol() + "  " + target.getAtom(i).getAtomTypeName() + "  " + 
					target.getAtom(i).getFlag(CDKConstants.ISAROMATIC));
		
		
		
		addStructure((IAtomContainer)target.clone());
		smrkMan.applyTransformation(target, reaction);
		
		//AtomContainerManipulator.clearAtomConfigurations(target);
		
		
		for (int i = 0; i < target.getAtomCount(); i++)
		{	
			target.getAtom(i).setFormalNeighbourCount(null);
			target.getAtom(i).setAtomTypeName(null);
			target.getAtom(i).setHybridization(null);
		}
			
			
		cfg.process(target);
		CDKHueckelAromaticityDetector.detectAromaticity(target);
		
		
		
		
		System.out.println("product:");
		for (int i = 0; i < target.getAtomCount(); i++)
			System.out.println(target.getAtom(i).getSymbol() + "  " + target.getAtom(i).getAtomTypeName() + "  " +
					target.getAtom(i).getFlag(CDKConstants.ISAROMATIC));
		for (int i = 0; i < target.getBondCount(); i++)
			System.out.println(target.getBond(i).getOrder() + "  " + target.getBond(i).getFlag(CDKConstants.ISAROMATIC));
		
		String transformedSmiles = SmartsHelper.moleculeToSMILES(target,true);
		
		
		addStructure(target);
		System.out.println("Reaction application: " + targetSmiles + "  -->  " + transformedSmiles);
		
		
		
		/*
		IAtomContainer pyr = MoleculeFactory.makePyridine();
		
		cfg.process(pyr);
		CDKHueckelAromaticityDetector.detectAromaticity(pyr);
		
		System.out.println("Pyridine:");
		for (int i = 0; i < pyr.getAtomCount(); i++)
			System.out.println(pyr.getAtom(i).getSymbol() + "  " + pyr.getAtom(i).getFlag(CDKConstants.ISAROMATIC));
		for (int i = 0; i < pyr.getBondCount(); i++)
			System.out.println(pyr.getBond(i).getOrder() + "  " + pyr.getBond(i).getFlag(CDKConstants.ISAROMATIC));
		
		addStructure(pyr);
		
		String pyrSmiles = SmartsHelper.moleculeToSMILES(target);
		System.out.println("Pyridine: " + pyrSmiles);
		*/
		
		
		
	}
	
	
	public void testSMIRKS(String smirks, IAtomContainer target) throws Exception
	{	
		
		System.out.println("Testing SMIRKS: " + smirks);
		SMIRKSManager smrkMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
		smrkMan.setFlagFilterEquivalentMappings(filterEqMaps);
		smrkMan.setSSMode(SmartsConst.SSM_NON_IDENTICAL);
		
		SMIRKSReaction reaction = smrkMan.parse(smirks);
		if (!smrkMan.getErrors().equals(""))
		{
			System.out.println(smrkMan.getErrors());
			return;
		}
		
		System.out.println(reaction.transformationDataToString());
		
		setFrame();
		String targetSmiles = SmartsHelper.moleculeToSMILES(target,true);
		
		addStructure((IAtomContainer)target.clone());
		smrkMan.applyTransformation(target, reaction);
		String transformedSmiles = SmartsHelper.moleculeToSMILES(target,true);
		addStructure(target);
		System.out.println("Reaction application: " + targetSmiles + "  -->  " + transformedSmiles);
		
	}
	
	public void testSMIRKS2(String smirks, String targetSmiles) throws Exception
	{
		
		
		System.out.println("Testing SMIRKS: " + smirks);
		SMIRKSManager smrkMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
		smrkMan.setFlagFilterEquivalentMappings(filterEqMaps);
		smrkMan.setSSMode(SmartsConst.SSM_NON_IDENTICAL);
		
		SMIRKSReaction reaction = smrkMan.parse(smirks);
		if (!smrkMan.getErrors().equals(""))
		{
			System.out.println(smrkMan.getErrors());
			return;
		}
		
		System.out.println(reaction.transformationDataToString());
		
		if (targetSmiles.equals(""))
			return;
		
		setFrame();
		
		IAtomContainer target = SmartsHelper.getMoleculeFromSmiles(targetSmiles);
		addStructure((IAtomContainer)target.clone());
		
		
		//smrkMan.applyTransformation(target, reaction);
		
		IAtomContainerSet products;
		
		if (FlagSingleCopyForPos)
			products = smrkMan.applyTransformationWithSingleCopyForEachPos(target, null, reaction);
		else
			products = smrkMan.applyTransformationWithCombinedOverlappedPos(target, null, reaction);
		
		
		for (int i = 0; i < products.getAtomContainerCount(); i++)
		{
			//String transformedSmiles = SmartsHelper.moleculeToSMILES(target);
			addStructure(products.getAtomContainer(i));
		}
		
		
		//System.out.println("Reaction application: " + targetSmiles + "  -->  " + transformedSmiles);
		
	}
	
	
	public IAtomContainer getMDLStruct(String mdlFile, int recNum)
	{	
		IAtomContainer res = null;
		
		AtomConfigurator  cfg = new AtomConfigurator();
		HydrogenAdderProcessor hadder = new HydrogenAdderProcessor();
		
		try
		{
			IChemObjectBuilder b = SilentChemObjectBuilder.getInstance();
			MyIteratingMDLReader reader = new MyIteratingMDLReader(new FileReader(mdlFile),b);
			int record=0;

			while (reader.hasNext()) 
			{	
				Object o = reader.next();
				record++;
				
				if (record == recNum)
				{	
					if (o instanceof IAtomContainer) 
					{
						IAtomContainer mol = (IAtomContainer)o;
						//if (mol.getAtomCount() == 0) continue;
						//AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
						//CDKHueckelAromaticityDetector.detectAromaticity(mol);
						
						//ToxTree processing
						hadder.process(mol);
						cfg.process(mol);
						CDKHueckelAromaticityDetector.detectAromaticity(mol);
						
						
						res = mol;
					}
					break;
				}
			}	
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
		
		return(res);
		
	}
	
}
