package ambit2.descriptors.test;

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

import ambit2.descriptors.constitutional.AtomCountRelativeDescriptor;
import ambit2.descriptors.constitutional.HeteroAtomCountDescriptor;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

import java.util.ArrayList;



public class TestDescriptors extends TestCase
{
	public SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
	
	public TestDescriptors()
	{	
	}
	
	public static Test suite() 
	{
		return new TestSuite(TestDescriptors.class);
	}
	
	public void performDescriptorTest(IMolecularDescriptor descriptor, int descrValueIndex, String info,
					ArrayList<String> smiles, ArrayList<Double> expectedValues, double eps_precision) throws Exception
	{
		assertEquals("smiles[] and expectedValues[] arrays must be of equal size()", smiles.size(), expectedValues.size());
		
		for (int i = 0; i < smiles.size(); i++)
		{	
			IAtomContainer mol = sp.parseSmiles(smiles.get(i));
			DescriptorValue dv = descriptor.calculate(mol);
			double d = TestDescriptors.unpackValueAsDouble(descrValueIndex, dv.getValue());
			assertEquals(info + " value for " +smiles.get(i), expectedValues.get(i).doubleValue(), d, eps_precision);
		}
	}
	
	
	public void testHeteroAtomCountDescriptor() throws Exception 
	{
		ArrayList<String> smiles = new ArrayList<String>();
		ArrayList<Double> expectedValues = new ArrayList<Double> ();
		
		IMolecularDescriptor descriptor = new HeteroAtomCountDescriptor();		
		smiles.add("NCCCCN"); 
		expectedValues.add(new Double(2));
		
		smiles.add("CCCC"); 
		expectedValues.add(new Double(0));
		
		smiles.add("ClC(Br)CCC=CO"); 
		expectedValues.add(new Double(3));
		
		performDescriptorTest(descriptor, 0, "HeteroAtomCountDescriptor", smiles, expectedValues, 1.0e-15);
	}
	
	public void testAtomCountRelativeDescriptor() throws Exception 
	{
		ArrayList<String> smiles = new ArrayList<String>();
		ArrayList<Double> expectedValues = new ArrayList<Double> ();
		
		IMolecularDescriptor descriptor = new AtomCountRelativeDescriptor();		
		smiles.add("CCCCN"); 
		expectedValues.add(new Double(0.0625));
		
		smiles.add("CCCC"); 
		expectedValues.add(new Double(0.0));
		
		performDescriptorTest(descriptor, 3, "AtomCountRelativeDescriptor", smiles, expectedValues, 1.0e-4);
	}
	
	
	
	//--------------------------------------------
	
	public static double unpackValueAsDouble(int index, IDescriptorResult result)
	{
		double d = 0.0;
		
		if (result instanceof DoubleResult)
		{
			d = ((DoubleResult)result).doubleValue();
			return d;
		}
		
		if (result instanceof DoubleArrayResult)
		{
			d = ((DoubleArrayResult)result).get(index);
			return d;
		}
		
		if (result instanceof BooleanResult)
		{
			boolean b = ((BooleanResult)result).booleanValue();
			if (b)
				d = 1.0;
			else
				d = 0.0;
			return d;
		}
		
		if (result instanceof IntegerResult)
		{
			d = ((IntegerResult)result).intValue();
			return d;
		}
		
		if (result instanceof IntegerArrayResult)
		{
			d = ((IntegerArrayResult)result).get(index);
			return d;
		}
		
		return d;
	}
	
	
}
