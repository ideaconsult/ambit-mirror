package ambit2.groupcontribution.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.BooleanResult;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.exceptions.EmptyMoleculeException;
import ambit2.groupcontribution.Calculator;
import ambit2.groupcontribution.GCMParser;
import ambit2.groupcontribution.GroupContributionModel;
import ambit2.groupcontribution.dataset.DataSet;
import ambit2.groupcontribution.descriptors.ILocalDescriptor;
import ambit2.groupcontribution.descriptors.LDAtomHybridization;
import ambit2.groupcontribution.descriptors.LDAtomSymbol;
import ambit2.groupcontribution.descriptors.LDAtomValency;
import ambit2.groupcontribution.descriptors.LDHNum;
import ambit2.groupcontribution.nmr.HNMRShifts;
import ambit2.groupcontribution.nmr.nmr_1h.HNMRKnowledgeBase;
import ambit2.groupcontribution.nmr.nmr_1h.HNMRPredefinedKnowledgeBase;
import ambit2.groupcontribution.nmr.nmr_1h.HShift;
import ambit2.smarts.SmartsHelper;


public class TestUtils2 
{
	public static GCMParser gcmParser = new GCMParser();
	
	public static void main(String[] args) throws Exception
	{
		//Set a model
		GroupContributionModel model = new GroupContributionModel();
		model.setModelType(GroupContributionModel.Type.ATOMIC);
		ArrayList<ILocalDescriptor> locDescr = new ArrayList<ILocalDescriptor>(); 
		locDescr.add(new LDAtomSymbol());
		//locDescr.add(new LDHNum());
		locDescr.add(new LDAtomValency());
		//locDescr.add(new LDAtomHybridization());
		model.setLocalDescriptors(locDescr);
		
		//testGroupCount("CC(C)CNCN", model);
		
		//testDescriptor(new AverageMolecularWeightDescriptor(), "[C][C][C]");
		//testDescriptor(new AverageMolecularWeightDescriptor(), "CCC");
		
		//testDataSet("/test-dataset.txt");
		//testDataSet("/Volumes/Data/test-dataset.csv");
		
		//testLocalDescriptorsParsing("FC,HeN,A,H,Val");
		
		//testHNMRKnowledgeBase();
		testHNMRKnowledgeBase("/Projects/HNMR/hnmr-knowledgebase.txt");
	}
	
	public static void testGroupCount(String smiles, GroupContributionModel model) throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);	
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		Map<String,Integer> groups = Calculator.getGroupsCount(mol, model);
		System.out.println("Group counts for " + smiles);
		for (Map.Entry<String, Integer> entry : groups.entrySet())		
			System.out.println(entry.getKey() + "   " + entry.getValue());
		
		
		System.out.println();
	}
	
	public static void testDescriptor(IMolecularDescriptor descriptor, String smiles) throws Exception 
	{
		SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer mol = sp.parseSmiles(smiles);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		DescriptorValue dv = descriptor.calculate(mol);
		
		double d = unpackValueAsDouble(0, dv.getValue());
		System.out.println(smiles + "  " + descriptor.getDescriptorNames()[0] + " = " + d);
	}
	
	public static double unpackValueAsDouble(int index, IDescriptorResult result) {
		double d = 0.0;

		if (result instanceof DoubleResult) {
			d = ((DoubleResult) result).doubleValue();
			return d;
		}

		if (result instanceof DoubleArrayResult) {
			d = ((DoubleArrayResult) result).get(index);
			return d;
		}

		if (result instanceof BooleanResult) {
			boolean b = ((BooleanResult) result).booleanValue();
			if (b)
				d = 1.0;
			else
				d = 0.0;
			return d;
		}

		if (result instanceof IntegerResult) {
			d = ((IntegerResult) result).intValue();
			return d;
		}

		if (result instanceof IntegerArrayResult) {
			d = ((IntegerArrayResult) result).get(index);
			return d;
		}

		return d;
	}
	
	public static void testDataSet(String fileName) throws Exception
	{
		File f = new File (fileName);
		DataSet dataSet = new DataSet(f);
		
		for (int i = 0; i < dataSet.dataObjects.size(); i++)
		{
			IAtomContainer mol = dataSet.dataObjects.get(i).molecule;
			System.out.println("  " + (i+1) + "  " + 
					SmartsHelper.moleculeToSMILES(mol, true) + "  " + 
					dataSet.dataObjects.get(i).getPropertiesAsString());
		}
	}
	
	public static void testLocalDescriptorsParsing(String locDescr) throws Exception
	{
		System.out.println("Parsing " + locDescr);
		List<ILocalDescriptor> descriptors = gcmParser.getLocalDescriptorsFromString(locDescr);
		if (!gcmParser.getErrors().isEmpty())
			System.out.println("Errors:\n" + gcmParser.getAllErrorsAsString());
		
		for (int i = 0; i < descriptors.size(); i++)
			System.out.println(descriptors.get(i).getShortName() + " " + descriptors.get(i).getName());
		
	}
	
	public static void testHNMRKnowledgeBase() throws EmptyMoleculeException
	{
		System.out.println("Testing HNMRPredefinedKnowledgeBase:");
		HNMRKnowledgeBase hnmrBase = HNMRPredefinedKnowledgeBase.getHNMRKnowledgeBase();
		hnmrBase.configure();
		
		if (hnmrBase.errors.isEmpty())
			System.out.println(hnmrBase.toString());
		else
			System.out.println("Errors:\n" + hnmrBase.getAllErrorsAsString());
		
	}
	
	public static void testHNMRKnowledgeBase(String fileName) throws Exception
	{	
		System.out.println("Testing HNMR Knowledge Base: " + fileName);
				
		HNMRKnowledgeBase hnmrBase = HNMRPredefinedKnowledgeBase.getHNMRKnowledgeBase(new File(fileName));
		hnmrBase.configure();
		
		if (hnmrBase.errors.isEmpty())
			System.out.println(hnmrBase.toString());
		else
			System.out.println("Errors:\n" + hnmrBase.getAllErrorsAsString());
		
	}
	
	public static void testHNMRShifts(String smiles, String knowledgeBaseFileName) throws Exception
	{	
		System.out.println("Configuring knowledge base ...");
		HNMRShifts hnmrShifts = new HNMRShifts(new  File(knowledgeBaseFileName));
		hnmrShifts.getSpinSplitManager().setFlagReportEquivalenceAtomCodes(true);
		//hnmrShifts.getSpinSplitManager().setNumLayers(4);
		
		System.out.println("Testing HNMR with molecule: " + smiles);
		IAtomContainer mol = TestUtils.moleculeBuilder(smiles);
		hnmrShifts.setStructure(mol);
		hnmrShifts.calculateHShifts();
		
		System.out.println("Log:\n" + hnmrShifts.getCalcLog());
		System.out.println();
		
		for (HShift hs : hnmrShifts.getHShifts())
			System.out.println(hs.toString());
		
	}
	
}
