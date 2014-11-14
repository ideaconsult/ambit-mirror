package ambit2.descriptors.test;

import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.BitSet;

import org.junit.Test;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.fingerprint.PubchemFingerprinter;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.ConnectionMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.io.MyIteratingMDLReader;
import ambit2.descriptors.fingerprints.PubChemFingerprinter;
import ambit2.descriptors.geometrical.CGIDescriptor;
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
		
		//calcCTI("NCCC", true);
		//calcCTI("OC1CC(N)CCC1", true);
		
		//calcCGI("/3D-str1-chair.mol", false);		
		//calcCTI("OC1CC(N)CCC1", false);
		
		//calcCTI("NCCN");
		//calcCTI("OC1=C(O)C2=C(C=C1)C3(OC(=O)C4=C3C=CC=C4)C5=C(O2)C(=C(O)C=C5)O");
		
		//calcPubChemDescriptor("CCCCCCCCCN");
		comparePubChemFP("/Users/nick/Projects/nci-basic.smi");
		
		//calcPubChemDescriptor("CCCC");
		
		//compareFP("");
		//compareFP("COC(=O)C1CCC(C)=CC1");
		//compareFP("CN1C(=O)C(=O)N2C=3C=CC=CC=3(C=C2(C1(=O)))");
		//compareFP("CCN(CC)CCNC=3C=CC(C)=C2C=3(C(=O)C1=CC=CC=C1S2)");
		
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
	
	public static void calcCGI(String molFile, boolean FlagPrintAtomAttrib) throws Exception
	{
		
		IAtomContainer mol =  loadMoleculeFromMDlFile(molFile);
		
		CGIDescriptor descr = new CGIDescriptor();
		DescriptorValue dValue = descr.calculate(mol);
		IDescriptorResult res = dValue.getValue();		
		double CGI = ((DoubleResult)res).doubleValue();
		
		System.out.println("mole file " + "  CTI = " + CGI);
		
		
		
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
	
	public static IAtomContainer loadMoleculeFromMDlFile(String molFile) throws Exception
	{
		IChemObjectBuilder b = SilentChemObjectBuilder.getInstance();
		MyIteratingMDLReader reader = new MyIteratingMDLReader(new FileReader(molFile),b);

		if (reader.hasNext()) 
		{	
			Object o = reader.next();
			if (o instanceof IAtomContainer) 
			{
				IAtomContainer mol = (IAtomContainer)o;
				//if (mol.getAtomCount() == 0) continue;
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
				CDKHueckelAromaticityDetector.detectAromaticity(mol);
				return mol;
			}
		}

		return null;
	}
	
	public static void calcPubChemDescriptor(String smiles) throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		
		PubChemFingerprinter pcfp = new PubChemFingerprinter();
		BitSet bitSet = pcfp.getFingerprint(mol);
		System.out.println("PubChem fp for " + smiles);
		System.out.println(bitSet);
		//System.out.println(getBitSetString(bitSet,881));
	}
	
	public static String getBitSetString(BitSet bs, int size)
	{	
		StringBuffer buf = new StringBuffer();
		for(int i = 1; i <= size; i++)
		{	
			if (bs.get(i))
				buf.append("1");
			else
				buf.append("0");
		}
		return(buf.toString());
	}
	
	public static void comparePubChemFP(String smiFile) throws Exception
	{
		File file = new File(smiFile);
		RandomAccessFile f = new RandomAccessFile(file,"r");			
		long length = f.length();
		
		PubChemFingerprinter ambitFP = new PubChemFingerprinter();
		PubchemFingerprinter cdkFP = new PubchemFingerprinter();
		long ambitTime = 0;
		long cdkTime = 0;
		long curTime = 0;
		int nStr = 0;
		int nDiff = 0;
		
		while (f.getFilePointer() < length)
		{	
			String line = f.readLine();
			line = line.trim();
			if (line.isEmpty())
				continue;
			IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(line);			
			nStr++;
			curTime = System.nanoTime();
			BitSet bs0 = ambitFP.getFingerprint(mol);
			ambitTime += (System.nanoTime() - curTime);
			
			/*
			curTime = System.nanoTime();
			BitSet bs1 = cdkFP.getFingerprint(mol);
			cdkTime += (System.nanoTime() - curTime);
			
			int diffBits = compareBitSets(bs0, bs1);
			if ( diffBits > 0)
			{	
				nDiff++;
				System.out.println("--> " + diffBits + "   " + line);
			}
			*/
				
			
			if (nStr % 100 == 0)
				System.out.println("nStr = " + nStr + "  nDiff = " + nDiff 
						+ "  ambitTime = " + ambitTime/1.0e9 + "  cdkTime = " + cdkTime/1.0e9 + "  cdk/ambit = " + 1.0*cdkTime/ambitTime);
		}	
		f.close();
	}
	
	public static int compareBitSets(BitSet bs0, BitSet bs1)
	{
		int nDiff = 0;
		int maxBit = bs0.length();
		if (maxBit < bs1.length())
			maxBit = bs1.length();
		for (int i = 0; i <= maxBit; i++)
		{
			if (bs0.get(i) != bs1.get(i))
				nDiff++;
		}
		return nDiff;
	}
	
	public static void compareFP(String smiles) throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		PubChemFingerprinter ambitFP = new PubChemFingerprinter();
		PubchemFingerprinter cdkFP = new PubchemFingerprinter();
		BitSet bs0 = ambitFP.getFingerprint(mol);
		BitSet bs1 = cdkFP.getFingerprint(mol);
		System.out.println(smiles);
		System.out.println("ambit: " + bs0);
		System.out.println("cdk:   " + bs1);
		bs1.xor(bs0);
		System.out.println("   diff:   " + bs1);
		
	}
	
	
	@Test
	public void test() {		
	}
}
