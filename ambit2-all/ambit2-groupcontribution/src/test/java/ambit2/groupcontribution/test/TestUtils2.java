package ambit2.groupcontribution.test;

import java.util.ArrayList;
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

import ambit2.descriptors.constitutional.AtomCountHybridizationDescriptor;
import ambit2.descriptors.constitutional.AverageMolecularWeightDescriptor;

import ambit2.groupcontribution.Calculator;
import ambit2.groupcontribution.GroupContributionModel;
import ambit2.groupcontribution.descriptors.ILocalDescriptor;
import ambit2.groupcontribution.descriptors.LDAtomHybridization;
import ambit2.groupcontribution.descriptors.LDAtomSymbol;
import ambit2.groupcontribution.descriptors.LDAtomValency;
import ambit2.groupcontribution.descriptors.LDHNum;
import ambit2.smarts.SmartsHelper;
import junit.framework.Assert;


public class TestUtils2 
{
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
		
		testDescriptor(new AverageMolecularWeightDescriptor(), "[C][C][C]");
		testDescriptor(new AverageMolecularWeightDescriptor(), "CCC");
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
}
