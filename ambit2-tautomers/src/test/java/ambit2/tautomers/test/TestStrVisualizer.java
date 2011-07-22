package ambit2.tautomers.test;

import java.util.Vector;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.templates.MoleculeFactory;


import javax.swing.*;

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
	//static SmilesParser smilesparser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
	static SmartsManager man = new SmartsManager();
	static IsomorphismTester isoTester = new IsomorphismTester();
	static SmartsToChemObject smToChemObj = new SmartsToChemObject();
	static ChemObjectToSmiles cots = new ChemObjectToSmiles(); 
	
	
	JFrame frame;
	int nStr = 0;
	int nCol = 3;	
	int size = 250;
	
	public static void main(String[] args) throws Exception 
	{
		//Vector<IAtomContainer> structs = new Vector<IAtomContainer> ();
		//structs.add(MoleculeFactory.make123Triazole());
		//structs.add(MoleculeFactory.makeAdenine());
		
		TestStrVisualizer tsv = new TestStrVisualizer(); 
		tsv.testSMIRKS("[N:1][C:2]([H])>>[N:1][H].[C:2]=[O]", "[H]CNC[H]");
		
		
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
		frame.setSize(700, 300);
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
	
	
	public void testSMIRKS(String smirks, String targetSmiles) throws Exception
	{
		System.out.println("Testing SMIRKS: " + smirks);
		SMIRKSManager smrkMan = new SMIRKSManager();
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
		smrkMan.applyTransformation(target, reaction);
		String transformedSmiles = SmartsHelper.moleculeToSMILES(target);
		addStructure(target);
		System.out.println("Reaction application: " + targetSmiles + "  -->  " + transformedSmiles);
		
		
		
		
	}
	
}
