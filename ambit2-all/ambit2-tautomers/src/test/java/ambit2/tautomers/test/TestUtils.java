package ambit2.tautomers.test;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.openscience.cdk.charges.AtomTypeCharges;
import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.charges.IChargeCalculator;
import org.openscience.cdk.charges.InductivePartialCharges;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.smarts.SmartsHelper;
import ambit2.tautomers.tools.TautomerAnalysis;

public class TestUtils {

	public static void main(String[] args) throws Exception
	{
		//testRanking(new double[] {2,3,3,0,45,1,3});
		
		testPartialCharges("CCC(O)=O");
	}
	
	
	public static void testRanking(double values[])
	{
		double ranks[] = TautomerAnalysis.getRanks(values, true);
		System.out.println("Original values + ranks:");
		for (int i = 0; i < ranks.length; i++)			
			System.out.println("  " + values[i] + "   rank = " + ranks[i]);
		
		int n = values.length;
		for (int i = 1; i < n; i++)
			for (int k = 0; k < n-i; k++)
			{
				if (ranks[k] >  ranks[k+1])
				{
					//swap ranks
					double tmp = ranks[k+1];
					ranks[k+1] = ranks[k];
					ranks[k] = tmp;
					//swap values
					tmp = values[k+1];
					values[k+1] = values[k];
					values[k] = tmp;
				}
			}
			
		
		System.out.println("\nValues sorted by ranking:");
		for (int i = 0; i < ranks.length; i++)			
			System.out.println("  " + values[i] + "   rank = " + ranks[i]);
		
		System.out.println("\nNumber of distinct values = " + TautomerAnalysis.getNumberOfDistinctValues(values));
	}
	
	
	public static void testPartialCharges(String smi) throws Exception
	{
		System.out.println("Test partial charges for " + smi);
		DecimalFormat df = new DecimalFormat("0.000", new DecimalFormatSymbols(new Locale("en-US")));
		
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smi, true);
		
		
		//IChargeCalculator chargeCalc = new GasteigerPEPEPartialCharges(); - not working --> only 0.0 charges
		//IChargeCalculator chargeCalc = new InductivePartialCharges(); - not working --> Exception
		//MMFF94PartialCharges present in CDK 2.3
		
		//GasteigerPEPEPartialCharges  gastPC = new GasteigerPEPEPartialCharges();		
		//gastPC.assignGasteigerPiPartialCharges(mol, true);		
		IChargeCalculator chargeCalc = new GasteigerMarsiliPartialCharges();
				
		
		
		chargeCalc.calculateCharges(mol);
		
		for (int i = 0; i < mol.getAtomCount(); i++)
		{	
			IAtom at = mol.getAtom(i);
			System.out.println("" + (i+1) + "  " + at.getSymbol() + "  " + df.format(at.getCharge()));
		}		
		
	}

}
