package ambit2.tautomers.test;

import java.io.FileReader;
import java.util.Vector;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;


import javax.swing.*;

import ambit2.core.io.MyIteratingMDLReader;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.HydrogenAdderProcessor;
import ambit2.smarts.ChemObjectToSmiles;
import ambit2.smarts.IAcceptable;
import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SMIRKSReaction;
import ambit2.smarts.SmartsConst;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsManager;
import ambit2.smarts.SmartsParser;
import ambit2.smarts.SmartsToChemObject;
import ambit2.smarts.StructInfo;
import ambit2.ui.Panel2D;


public class TestStrVisualizer 
{
	
	static SmartsParser sp = new SmartsParser();
	//static SmilesParser smilesparser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
	static SmartsManager man = new SmartsManager();
	static IsomorphismTester isoTester = new IsomorphismTester();
	static SmartsToChemObject smToChemObj = new SmartsToChemObject();
	static ChemObjectToSmiles cots = new ChemObjectToSmiles(); 
	
	boolean filterEqMaps = true;
	boolean FlagSingleCopyForPos = false;
	JFrame frame;
	int nStr = 0;
	int nCol = 4;	
	int size = 250;
	
	public static void main(String[] args) throws Exception 
	{
		//Vector<IAtomContainer> structs = new Vector<IAtomContainer> ();
		//structs.add(MoleculeFactory.make123Triazole());
		//structs.add(MoleculeFactory.makeAdenine());
		
		TestStrVisualizer tsv = new TestStrVisualizer(); 
		//tsv.testSMIRKS2("[N:1][C:2]([H])>>[N:1][H].[C:2]=[O]", "c1cc(OCCN(C([H])([H])S)(C([H])Cl))ccc1C(c1ccc(OCNC[H])cc1)=C(CC)c1ccccc1");
		//tsv.testSMIRKS2("[N:1][C:2]([H])>>[N:1][H].[C:2]=[O]", "c1cc(OCCN(C([H])([H])S)(C([H])([H])Cl))ccc1CC([H])NC[H]");
		
		//tsv.testSMIRKS("[#7:1][#6:2]>>[#7+:1]([O-])[#6:2]","CCN");
		tsv.testSMIRKS("[#7:1][#6:2]>>[#7+:1]([O-])[#6:2]","CCN", false);
		
		
		//tsv.testSMIRKS("[N:1][C:2]([H])>>[N:1][H].[C:2]=[O]", 
		//		tsv.getMDLStruct("D:/Projects/nina/test-smirks-structs/4_hydroxytamoxifen.sdf",1));
		
	}
	
	TestStrVisualizer()
	{	
	}
	
	TestStrVisualizer(Vector<IAtomContainer> structs)
	{
		setFrame();
		for (int i = 0; i < structs.size(); i++)
			addStructure(structs.get(i));
	}
	
	
	void setFrame()
	{
		frame = new JFrame();
		frame.setSize(1000, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setVisible(true);
	}
	
	void addStructure(IAtomContainer struct)
	{	
		Panel2D p = new Panel2D();
		p.setAtomContainer(struct);
		frame.getContentPane().add(p);
		p.setBounds((nStr%nCol)*size,(nStr/nCol)*size,size-2,size-2);
		frame.setVisible(true);
		nStr++;
	}
	
	
	public void testSMIRKS(String smirks, String targetSmiles, boolean hAdd) throws Exception
	{
		AtomConfigurator  cfg = new AtomConfigurator();
		HydrogenAdderProcessor hadder = new HydrogenAdderProcessor();
		
		System.out.println("Testing SMIRKS: " + smirks);
		SMIRKSManager smrkMan = new SMIRKSManager();
		smrkMan.FlagFilterEquivalentMappings = filterEqMaps;
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
		
		
		
		addStructure((IAtomContainer)target.clone());
		smrkMan.applyTransformation(target, reaction);
		
		String transformedSmiles = SmartsHelper.moleculeToSMILES(target);
		addStructure(target);
		System.out.println("Reaction application: " + targetSmiles + "  -->  " + transformedSmiles);
		
	}
	
	
	public void testSMIRKS(String smirks, IAtomContainer target) throws Exception
	{	
		
		System.out.println("Testing SMIRKS: " + smirks);
		SMIRKSManager smrkMan = new SMIRKSManager();
		smrkMan.FlagFilterEquivalentMappings = filterEqMaps;
		smrkMan.setSSMode(SmartsConst.SSM_NON_IDENTICAL);
		
		SMIRKSReaction reaction = smrkMan.parse(smirks);
		if (!smrkMan.getErrors().equals(""))
		{
			System.out.println(smrkMan.getErrors());
			return;
		}
		
		System.out.println(reaction.transformationDataToString());
		
		setFrame();
		String targetSmiles = SmartsHelper.moleculeToSMILES(target);
		
		addStructure((IAtomContainer)target.clone());
		smrkMan.applyTransformation(target, reaction);
		String transformedSmiles = SmartsHelper.moleculeToSMILES(target);
		addStructure(target);
		System.out.println("Reaction application: " + targetSmiles + "  -->  " + transformedSmiles);
		
	}
	
	public void testSMIRKS2(String smirks, String targetSmiles) throws Exception
	{
		
		
		System.out.println("Testing SMIRKS: " + smirks);
		SMIRKSManager smrkMan = new SMIRKSManager();
		smrkMan.FlagFilterEquivalentMappings = filterEqMaps;
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
			IChemObjectBuilder b = DefaultChemObjectBuilder.getInstance();
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
