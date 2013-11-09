package ambit2.descriptors.test;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;
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

import ambit2.descriptors.constitutional.AtomCountHybridizationDescriptor;
import ambit2.descriptors.constitutional.AtomCountRelativeDescriptor;
import ambit2.descriptors.constitutional.AverageMolecularWeightDescriptor;
import ambit2.descriptors.constitutional.CubicRootMolecularWeightDescriptor;
import ambit2.descriptors.constitutional.HeteroAtomCountDescriptor;
import ambit2.descriptors.constitutional.RotatableBondFractionDescriptor;
import ambit2.descriptors.constitutional.SquareRootMolecularWeightDescriptor;



public class TestDescriptors  {
	public SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());

	public void performDescriptorTest(IMolecularDescriptor descriptor, int descrValueIndex, String info,
			ArrayList<String> smiles, ArrayList<Double> expectedValues, double eps_precision) throws Exception
			{
		Assert.assertEquals("smiles[] and expectedValues[] arrays must be of equal size()", smiles.size(), expectedValues.size());

		for (int i = 0; i < smiles.size(); i++)
		{	
			IAtomContainer mol = sp.parseSmiles(smiles.get(i));
			DescriptorValue dv = descriptor.calculate(mol);
			double d = TestDescriptors.unpackValueAsDouble(descrValueIndex, dv.getValue());
			Assert.assertEquals(info + " value for " +smiles.get(i), expectedValues.get(i).doubleValue(), d, eps_precision);
		}
	}
	
	@Test
	public void testAtomCountHybridizationDescriptor() throws Exception 
	{
		ArrayList<String> smiles = new ArrayList<String>();
		ArrayList<Double> expectedValues1 = new ArrayList<Double> ();
		ArrayList<Double> expectedValues2 = new ArrayList<Double> ();
		ArrayList<Double> expectedValues3 = new ArrayList<Double> ();

		IMolecularDescriptor descriptor = new AtomCountHybridizationDescriptor();		

		smiles.add("CCC"); 
		expectedValues1.add(new Double(3));
		expectedValues2.add(new Double(0));
		expectedValues3.add(new Double(0));

		smiles.add("CC=CCC=CC"); 
		expectedValues1.add(new Double(3));
		expectedValues2.add(new Double(4));
		expectedValues3.add(new Double(0));

		smiles.add("CC(C)=CC(=O)CC(C)CC=O"); 
		expectedValues1.add(new Double(6));
		expectedValues2.add(new Double(6));
		expectedValues3.add(new Double(0));

		smiles.add("C#CCC=CCC"); 
		expectedValues1.add(new Double(3));
		expectedValues2.add(new Double(2));
		expectedValues3.add(new Double(2));

		smiles.add("C(O)C(=O)O"); 
		expectedValues1.add(new Double(3));
		expectedValues2.add(new Double(2));
		expectedValues3.add(new Double(0));

		smiles.add("C(O)C(=O)Br"); 
		expectedValues1.add(new Double(3));
		expectedValues2.add(new Double(2));
		expectedValues3.add(new Double(0));

		smiles.add("CC(C)=CC(=O)CC(C)CC=O"); 
		expectedValues1.add(new Double(6));
		expectedValues2.add(new Double(6));
		expectedValues3.add(new Double(0));


		performDescriptorTest(descriptor, 0, "AtomCountHybridizationDescriptor", smiles, expectedValues1, 1.0e-2);
		performDescriptorTest(descriptor, 1, "AtomCountHybridizationDescriptor", smiles, expectedValues2, 1.0e-2);
		performDescriptorTest(descriptor, 2, "AtomCountHybridizationDescriptor", smiles, expectedValues3, 1.0e-2);

	}
	@Test
	public void testAtomCountRelativeDescriptor() throws Exception	{
		ArrayList<String> smiles = new ArrayList<String>();

		ArrayList<Double> expectedValues1 = new ArrayList<Double> ();
		ArrayList<Double> expectedValues2 = new ArrayList<Double> ();
		ArrayList<Double> expectedValues3 = new ArrayList<Double> ();
		ArrayList<Double> expectedValues4 = new ArrayList<Double> ();
		ArrayList<Double> expectedValues5 = new ArrayList<Double> ();

		IMolecularDescriptor descriptor = new AtomCountRelativeDescriptor();	


		smiles.add("CCCC"); 
		expectedValues1.add(new Double(0.71));
		expectedValues2.add(new Double(0.29));
		expectedValues3.add(new Double(0.0));
		expectedValues4.add(new Double(0.0));
		expectedValues5.add(new Double(0.0));


		smiles.add("CNCC"); 
		expectedValues1.add(new Double(0.69));
		expectedValues2.add(new Double(0.23));
		expectedValues3.add(new Double(0.0));
		expectedValues4.add(new Double(0.08));
		expectedValues5.add(new Double(0.0));

		smiles.add("CCO"); 
		expectedValues1.add(new Double(0.67));
		expectedValues2.add(new Double(0.22));
		expectedValues3.add(new Double(0.11));
		expectedValues4.add(new Double(0.0));
		expectedValues5.add(new Double(0.0));

		smiles.add("CC(Cl)CO"); 
		expectedValues1.add(new Double(0.58));
		expectedValues2.add(new Double(0.25));
		expectedValues3.add(new Double(0.08));
		expectedValues4.add(new Double(0.0));
		expectedValues5.add(new Double(0.08));

		smiles.add("CCBr"); 
		expectedValues1.add(new Double(0.63));
		expectedValues2.add(new Double(0.25));
		expectedValues3.add(new Double(0.0));
		expectedValues4.add(new Double(0.0));
		expectedValues5.add(new Double(0.13));

		smiles.add("CC(Br)C(Cl)CC(O)CNO"); 
		expectedValues1.add(new Double(0.54));
		expectedValues2.add(new Double(0.25));
		expectedValues3.add(new Double(0.08));
		expectedValues4.add(new Double(0.04));
		expectedValues5.add(new Double(0.08));


		performDescriptorTest(descriptor, 0, "AtomCountRelativeDescriptor", smiles, expectedValues1, 1.0e-2);
		performDescriptorTest(descriptor, 1, "AtomCountRelativeDescriptor", smiles, expectedValues2, 1.0e-2);
		performDescriptorTest(descriptor, 2, "AtomCountRelativeDescriptor", smiles, expectedValues3, 1.0e-2);
		performDescriptorTest(descriptor, 3, "AtomCountRelativeDescriptor", smiles, expectedValues4, 1.0e-2);
		performDescriptorTest(descriptor, 4, "AtomCountRelativeDescriptor", smiles, expectedValues5, 1.0e-2);

	}
	@Test
	public void testAverageMolecularWeightDescriptor() throws Exception	{
		ArrayList<String> smiles = new ArrayList<String>();
		ArrayList<Double> expectedValues = new ArrayList<Double> ();

		IMolecularDescriptor descriptor = new AverageMolecularWeightDescriptor();		

		smiles.add("CCC"); 
		expectedValues.add(new Double(4.00875));

		smiles.add("CC(=O)C=CC=O"); 
		expectedValues.add(new Double(7.54618));

		smiles.add("C1CCC1"); 
		expectedValues.add(new Double(4.67560));

		smiles.add("C1CCCCC1"); 
		expectedValues.add(new Double(4.67560));

		smiles.add("C(O)C(=O)O"); 
		expectedValues.add(new Double(8.45007));

		smiles.add("C(O)(=O)CBr"); 
		expectedValues.add(new Double(17.36845));

		smiles.add("CCN(CC)N=O"); 
		expectedValues.add(new Double(5.73022));


		performDescriptorTest(descriptor, 0, "AverageMolecularWeightDescriptor", smiles, expectedValues, 1.0e-0);

	}
	@Test
	public void testCubicRootMolecularWeightDescriptor() throws Exception	{
		ArrayList<String> smiles = new ArrayList<String>();
		ArrayList<Double> expectedValues = new ArrayList<Double> ();

		IMolecularDescriptor descriptor = new CubicRootMolecularWeightDescriptor();		

		smiles.add("CCC"); 
		expectedValues.add(new Double(3.5329193));

		smiles.add("CC(=O)C=CC=O"); 
		expectedValues.add(new Double(4.6120102));

		smiles.add("C1CCC1"); 
		expectedValues.add(new Double(3.82830207));

		smiles.add("C1CCCCC1"); 
		expectedValues.add(new Double(4.38231191));

		smiles.add("C(O)C(=O)O"); 
		expectedValues.add(new Double(4.236763));

		smiles.add("C(O)(=O)CBr"); 
		expectedValues.add(new Double(5.179451699));

		smiles.add("CCN(CC)N=O"); 
		expectedValues.add(new Double(4.6897301));


		performDescriptorTest(descriptor, 0, "CubicRootMolecularWeightDescriptor", smiles, expectedValues, 1.0e-1);

	}
	@Test
	public void testHeteroAtomCountDescriptor() throws Exception	{
		ArrayList<String> smiles = new ArrayList<String>();
		ArrayList<Double> expectedValues = new ArrayList<Double> ();

		IMolecularDescriptor descriptor = new HeteroAtomCountDescriptor();		
		smiles.add("NCCCCN"); 
		expectedValues.add(new Double(2));

		smiles.add("CCCC"); 
		expectedValues.add(new Double(0));

		performDescriptorTest(descriptor, 0, "HeteroAtomCountDescriptor", smiles, expectedValues, 1.0e-15);
	}
	@Test
	public void testRotatableBondFractionDescriptor() throws Exception 
	{
		ArrayList<String> smiles = new ArrayList<String>();
		ArrayList<Double> expectedValues = new ArrayList<Double> ();

		IMolecularDescriptor descriptor = new RotatableBondFractionDescriptor();		
		smiles.add("BrCCNCCOCF"); 
		expectedValues.add(new Double(0.75));

		smiles.add("CC(C)CCC"); 
		expectedValues.add(new Double(0.4));

		smiles.add("CCCC"); 
		expectedValues.add(new Double(0.33));

		performDescriptorTest(descriptor, 0, "RotatableBondFractionDescriptor", smiles, expectedValues, 1.0e-02);
	}
	@Test
	public void testSquareRootMolecularWeightDescriptor() throws Exception 
	{
		ArrayList<String> smiles = new ArrayList<String>();
		ArrayList<Double> expectedValues = new ArrayList<Double> ();

		IMolecularDescriptor descriptor = new SquareRootMolecularWeightDescriptor();		

		smiles.add("CCC"); 
		expectedValues.add(new Double(6.640697));

		smiles.add("CC(=O)C=CC=O"); 
		expectedValues.add(new Double(9.9045446));

		smiles.add("C1CCC1"); 
		expectedValues.add(new Double(7.490974));

		smiles.add("C1CCCCC1"); 
		expectedValues.add(new Double(9.17391961));

		smiles.add("C(O)C(=O)O"); 
		expectedValues.add(new Double(8.7203995));

		smiles.add("C(O)(=O)CBr"); 
		expectedValues.add(new Double(11.7876179));

		smiles.add("CCN(CC)N=O"); 
		expectedValues.add(new Double(10.1559785));


		performDescriptorTest(descriptor, 0, "SquareRootMolecularWeightDescriptor", smiles, expectedValues, 1.0e-1);

	}


	/*	later
	public void testRingDescriptors() throws Exception 
	{
		ArrayList<String> smiles = new ArrayList<String>();

		ArrayList<Double> expectedValues1 = new ArrayList<Double> ();
		ArrayList<Double> expectedValues2 = new ArrayList<Double> ();
		ArrayList<Double> expectedValues3 = new ArrayList<Double> ();
		ArrayList<Double> expectedValues4 = new ArrayList<Double> ();
		ArrayList<Double> expectedValues5 = new ArrayList<Double> ();
		ArrayList<Double> expectedValues6 = new ArrayList<Double> ();
		ArrayList<Double> expectedValues7 = new ArrayList<Double> ();
		ArrayList<Double> expectedValues8 = new ArrayList<Double> ();
		ArrayList<Double> expectedValues9 = new ArrayList<Double> ();

		IMolecularDescriptor descriptor = new RingDescriptors();	


		smiles.add("O=C(c1ccc(cc1O)OC)c2ccccc2O"); 
		expectedValues1.add(new Double(0.667));
		expectedValues2.add(new Double(0));
		expectedValues3.add(new Double(2));
		expectedValues4.add(new Double(1));
		expectedValues5.add(new Double(1));
		expectedValues6.add(new Double(12));
		expectedValues7.add(new Double(12));
		expectedValues8.add(new Double(0));
		expectedValues9.add(new Double(1));


		smiles.add("OS(=O)(=O)c1cc(c(O)cc1OC)C(=O)c2ccccc2"); 
		expectedValues1.add(new Double(0.571));
		expectedValues2.add(new Double(0));
		expectedValues3.add(new Double(2));
		expectedValues4.add(new Double(1));
		expectedValues5.add(new Double(1));
		expectedValues6.add(new Double(12));
		expectedValues7.add(new Double(12));
		expectedValues8.add(new Double(0));
		expectedValues9.add(new Double(1));

		smiles.add("O=C(c1ccc(cc1O)OC)c2ccccc2"); 
		expectedValues1.add(new Double(0.706));
		expectedValues2.add(new Double(0));
		expectedValues3.add(new Double(2));
		expectedValues4.add(new Double(1));
		expectedValues5.add(new Double(1));
		expectedValues6.add(new Double(12));
		expectedValues7.add(new Double(12));
		expectedValues8.add(new Double(0));
		expectedValues9.add(new Double(1));

		smiles.add("OC1C(O)C(O)C(=O)OC1CO"); 
		expectedValues1.add(new Double(0.500));
		expectedValues2.add(new Double(0));
		expectedValues3.add(new Double(1));
		expectedValues4.add(new Double(1));
		expectedValues5.add(new Double(1));
		expectedValues6.add(new Double(6));
		expectedValues7.add(new Double(6));
		expectedValues8.add(new Double(0));
		expectedValues9.add(new Double(1));

		smiles.add("COc1ccc(/C=C/C(=O)OCC(CC)CCCC)cc1"); 
		expectedValues1.add(new Double(0.286));
		expectedValues2.add(new Double(0));
		expectedValues3.add(new Double(1));
		expectedValues4.add(new Double(1));
		expectedValues5.add(new Double(1));
		expectedValues6.add(new Double(6));
		expectedValues7.add(new Double(6));
		expectedValues8.add(new Double(0));
		expectedValues9.add(new Double(1));



		performDescriptorTest(descriptor, 0, "RingDescriptors", smiles, expectedValues1, 1.0e-3);
		performDescriptorTest(descriptor, 1, "RingDescriptors", smiles, expectedValues2, 1.0e-2);
		performDescriptorTest(descriptor, 2, "RingDescriptors", smiles, expectedValues3, 1.0e-2);
		performDescriptorTest(descriptor, 3, "RingDescriptors", smiles, expectedValues4, 1.0e-2);
		performDescriptorTest(descriptor, 4, "RingDescriptors", smiles, expectedValues5, 1.0e-3);
		performDescriptorTest(descriptor, 5, "RingDescriptors", smiles, expectedValues6, 1.0e-2);
		performDescriptorTest(descriptor, 6, "RingDescriptors", smiles, expectedValues7, 1.0e-2);
		performDescriptorTest(descriptor, 7, "RingDescriptors", smiles, expectedValues8, 1.0e-2);
		performDescriptorTest(descriptor, 8, "RingDescriptors", smiles, expectedValues9, 1.0e-2);

	}


	public void testNonHAtomNumber() throws Exception 
	{
		ArrayList<String> smiles = new ArrayList<String>();
		ArrayList<Double> expectedValues = new ArrayList<Double> ();

		IMolecularDescriptor descriptor = new NonHAtomNumber();		

		smiles.add("[H][H]"); 
		expectedValues.add(new Double(0));

		smiles.add("C[CH2]"); 
		expectedValues.add(new Double(2));

		smiles.add("C(Cl)C[CH2]"); 
		expectedValues.add(new Double(4));

		smiles.add("ClCCB"); 
		expectedValues.add(new Double(4));

		smiles.add("CC(Br)C(Cl)CC(O)CNO"); 
		expectedValues.add(new Double(11));


		performDescriptorTest(descriptor, 0, "NonHAtomNumber", smiles, expectedValues, 1.0e-0);

	}

	public void testBondNumber() throws Exception 
	{
		ArrayList<String> smiles = new ArrayList<String>();
		ArrayList<Double> expectedValues1 = new ArrayList<Double> ();
		ArrayList<Double> expectedValues2 = new ArrayList<Double> ();

		IMolecularDescriptor descriptor = new BondNumber();		

		smiles.add("[H][H]"); 
		expectedValues1.add(new Double(1));
		expectedValues2.add(new Double(0));


		smiles.add("C[CH2]"); 
		expectedValues1.add(new Double(6));
		expectedValues2.add(new Double(1));


		smiles.add("CC(C)=CC(=O)CC(C)CC=O"); 
		expectedValues1.add(new Double(27));
		expectedValues2.add(new Double(11));

		smiles.add("C(Cl)C[CH2]"); 
		expectedValues1.add(new Double(9));
		expectedValues2.add(new Double(3));

		smiles.add("C(O)C(=O)Br"); 
		expectedValues1.add(new Double(7));
		expectedValues2.add(new Double(4));


		smiles.add("CC(Br)C(Cl)CC(O)CNO"); 
		expectedValues1.add(new Double(23));
		expectedValues2.add(new Double(10));


		performDescriptorTest(descriptor, 0, "BondNumber", smiles, expectedValues1, 1.0e-2);
		performDescriptorTest(descriptor, 1, "BondNumber", smiles, expectedValues2, 1.0e-2);


	}

	 */

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
