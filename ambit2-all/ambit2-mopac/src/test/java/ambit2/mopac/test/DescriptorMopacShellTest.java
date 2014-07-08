package ambit2.mopac.test;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleArrayResult;

import ambit2.core.smiles.SmilesParserWrapper;
import ambit2.core.smiles.SmilesParserWrapper.SMILES_PARSER;
import ambit2.mopac.AbstractMopacShell;
import ambit2.mopac.DescriptorMopacShell;
import ambit2.mopac.Mopac7Reader;

public class DescriptorMopacShellTest {
	protected SmilesParserWrapper parser;
	
	@Before
	public void setup() throws Exception {
		parser =  SmilesParserWrapper.getInstance(SMILES_PARSER.CDK);		
	}
	/*
	 * 
	 */
	@Test
	public void testCalculate() throws Exception {
		DescriptorMopacShell d = new DescriptorMopacShell();

		String smiles = "c1ccccc1";
		//"[H]C1=C([H])C([H])=C([H])C([H])=C1([H])";
		//todo add hydrogens
		IAtomContainer ac = parser.parseSmiles(smiles);
		DescriptorValue v = (DescriptorValue) d.calculate(ac);
		if (v.getException() != null) throw v.getException();
		
		Assert.assertEquals(Mopac7Reader.parameters.length,v.getNames().length);
		Assert.assertEquals(DescriptorMopacShell.EHOMO,v.getNames()[7]);
		Assert.assertEquals(DescriptorMopacShell.ELUMO,v.getNames()[8]);
		
		DoubleArrayResult r = (DoubleArrayResult) v.getValue();
		if (v.getException() != null) throw v.getException();
		Assert.assertEquals(-9.75051,r.get(7),1E-2); //ehomo
		Assert.assertEquals(0.39523,r.get(8),1E-2); //elumo
		Assert.assertEquals(78.113,r.get(6),1E-2); //Molecular weight
		Assert.assertEquals(97.85217,r.get(2),1E-2); //FINAL HEAT OF FORMATION
	}
	
	@Test
	public void testCalculate2() throws Exception {
		DescriptorMopacShell d = new DescriptorMopacShell();

		String smiles = "[O-][N+](=O)C1=CC(Cl)=C(Cl)C=C1Cl";
		//"[H]C1=C([H])C([H])=C([H])C([H])=C1([H])";
		//todo add hydrogens
		IAtomContainer ac = parser.parseSmiles(smiles);
		DescriptorValue v = (DescriptorValue) d.calculate(ac);
		if (v.getException() != null) throw v.getException();
		Assert.assertEquals(Mopac7Reader.parameters.length,v.getNames().length);
		Assert.assertEquals(DescriptorMopacShell.EHOMO,v.getNames()[7]);
		Assert.assertEquals(DescriptorMopacShell.ELUMO,v.getNames()[8]);
		
		DoubleArrayResult r = (DoubleArrayResult) v.getValue();
		//System.out.println(r.get(7));
		//System.out.println(r.get(8));

		Assert.assertEquals(-9.70887,r.get(7),1E-2); //ehomo
		Assert.assertEquals(-3.54057,r.get(8),1E-2); //elumo
	//	Assert.assertEquals(78.113,r.get(6),1E-2); //Molecular weight
	//	Assert.assertEquals(97.85217,r.get(2),1E-2); //FINAL HEAT OF FORMATION
	}
	

	@Test
	public void testCalculateUnsupportedAtom() throws Exception {
		DescriptorMopacShell d = new DescriptorMopacShell();
		//todo add hydrogens
		IAtomContainer ac = parser.parseSmiles("C[Si]");
		DescriptorValue value = d.calculate(ac);
		Assert.assertNotNull(value.getException());
		Assert.assertEquals(AbstractMopacShell.MESSAGE_UNSUPPORTED_TYPE + "Si",value.getException().getMessage());
		
	}
	@Test
	public void testCalculate1() throws Exception  {
		DescriptorMopacShell d = new DescriptorMopacShell();
		//todo add H
		IAtomContainer ac = parser.parseSmiles("CCCCCCCCCCC");
		DescriptorValue v = (DescriptorValue) d.calculate(ac);
		if (v.getException() != null) throw v.getException();
		Assert.assertEquals(Mopac7Reader.parameters.length,v.getNames().length);
		Assert.assertEquals(DescriptorMopacShell.EHOMO,v.getNames()[7]);
		Assert.assertEquals(DescriptorMopacShell.ELUMO,v.getNames()[8]);

	}
	/*
	 * copy descriptor options classes from toxtree
	public void testOptions() throws Exception {
		IDescriptor d = new DescriptorMopacShell();
		DescriptorOptions o = new DescriptorOptions();
		o.setObject(d);
		JOptionPane.showConfirmDialog(null,o);
		o.setObject(d);
		JOptionPane.showConfirmDialog(null,o);
		
		d = new XLogPDescriptor();
		
		o.setObject(d);
		JOptionPane.showConfirmDialog(null,o);		
	}	
	*/

}
