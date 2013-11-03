package ambit2.descriptors.test;

import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.ConnectionMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import ambit2.descriptors.topological.CTIDescriptor;
import ambit2.smarts.SmartsHelper;

public class TestUtilities 
{
	public static void main(String[] args) throws Exception
	{
		/*
		calcCTI("CCCCC", false);
		calcCTI("CC(C)CC", false);
		calcCTI("CC(C)(C)C", false);
		calcCTI("NCCCC", false);
		calcCTI("CNCCC", false);
		calcCTI("CCNCC", false);
		calcCTI("NC(C)CC", false);
		calcCTI("CN(C)CC", false);
		calcCTI("CC(C)NC", false);
		calcCTI("CC(C)CN", false);
		calcCTI("NC(C)(C)C", false);
		*/
		
		//calcCTI("CCCN", true);
		calcCTI("OC1CC(N)CCC1", true);
		
		
		//calcCTI("NCCN");
		//calcCTI("OC1=C(O)C2=C(C=C1)C3(OC(=O)C4=C3C=CC=C4)C5=C(O2)C(=C(O)C=C5)O");
	}
	
	public static void calcCTI(String smiles, boolean FlagPrintAtomAttrib) throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles, false);
		CTIDescriptor descr = new CTIDescriptor();
		DescriptorValue dValue = descr.calculate(mol);
		IDescriptorResult res = dValue.getValue();		
		double CTI = ((DoubleResult)res).doubleValue();
		System.out.println(smiles + "  CTI = " + CTI);
		
		if (FlagPrintAtomAttrib)
		{	
			for (int i = 0; i < mol.getAtomCount(); i++) 
			{
				IAtom a = mol.getAtom(i);
				System.out.println("Atom " + i + "  L0=" 
						+  a.getValency() + "  H=" + a.getImplicitHydrogenCount() 
						+ "  q=" + a.getCharge());
			}
			
			double[][] matr = ConnectionMatrix.getMatrix(mol);        
	        int[][] D = PathTools.computeFloydAPSP(matr);
	        
	        for (int i = 0; i < mol.getAtomCount(); i++)
	        {	
	        	for (int j = 0; j < mol.getAtomCount(); j++)
	        		System.out.print(" " + D[i][j]);
	        		
	        	System.out.println();
	        }	
	        
		}	
	}
}
