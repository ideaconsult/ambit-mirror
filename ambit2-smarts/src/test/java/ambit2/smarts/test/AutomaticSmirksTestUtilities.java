package ambit2.smarts.test;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import ambit2.core.data.MoleculeTools;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SMIRKSReaction;
import ambit2.smarts.SmartsConst;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;


public class AutomaticSmirksTestUtilities 
{
	enum AST_TASK {
		task1, task2
	}
	
	public AST_TASK task = AST_TASK.task1;
	public String endLine = "\r\n";
	public String outFileName = null;
	public String inFileName = "";
	public int maxNumOfProcessLines = 0;
	public int startLine = 1;
	
	public RandomAccessFile outFile = null;
	int curLine = 0;
	int curProcessedLine = 0;
	int nErrors = 0;
	
	SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
	
	public static void main(String[] args)
	{
		AutomaticSmirksTestUtilities ast = new AutomaticSmirksTestUtilities();
		ast.inFileName = "/Volumes/Data/Projects/AmbitSMIRKS2016/stereo-errors__.txt";
		ast.startLine = 2;
		ast.maxNumOfProcessLines = 200;
		ast.run();
	}
	
	//Input - Output handling 
	
	int openOutputFile()
	{
		try
		{
			File file = new File(outFileName);
			outFile = new RandomAccessFile(file,"rw");
			outFile.setLength(0);
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}

		if (outFile == null)
			System.out.println("Incorrect outFile");

		return(0);
	}


	int closeOutputFile() 
	{
		try
		{
			if (outFile != null)
				outFile.close();
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}

		return(0);
	}
	
	void run()
	{
		if (outFileName != null)
			openOutputFile();
		
		iterateInputFile();
		
		closeOutputFile();
	}

	void iterateInputFile()
	{	
		try
		{	
			File file = new File(inFileName);

			/*
				if (FlagWorkWithWholeDirectory)
					if (file.isDirectory())
					{	
						iterateDir(file);
						return;
					}	
			 */
			RandomAccessFile f = new RandomAccessFile(file,"r");			
			long length = f.length();


			int n = 0;
			curProcessedLine = 0;
			while (f.getFilePointer() < length)
			{	
				n++;
				curLine = n;

				String line = f.readLine();
				//System.out.println("line " + n + "  " + line);

				
				if (n < startLine)
						continue;

				curProcessedLine++;

				if (maxNumOfProcessLines > 0)
						if (curProcessedLine > maxNumOfProcessLines) 
							break;
				

				processLine(line.trim());
			}


			f.close();
		}
		catch (Exception e)
		{	
			System.out.println(e.getMessage());

		}
	}


	int processLine(String line)
	{
		try{
		switch (task)
		{
		case task1:
			processLine1(line);
			break;
		}
		}
		catch (Exception e)
		{
			
		}
		return 0;
	}
	
	int processLine1(String line) throws Exception
	{
		System.out.println();
		System.out.println("Line " + curLine);
		 
		Task1LineData t1ld = Task1LineData.parseLine(line);
		System.out.println("smirks = " + t1ld.smirks);
		System.out.println("substrate      = " + t1ld.substrate);
		System.out.println("substrate(kek) = " + t1ld.kekulizedSubstrate);
		
		String products[] = t1ld.chemaxonProducts.split(";");
		String abs_products[] = new String[products.length];
				
		System.out.println("chemaxonProducts:"); 
		for (int i = 0; i < products.length; i++)
		{
			IAtomContainer mol = smilesParser.parseSmiles(products[i]);
			abs_products[i] = SmilesGenerator.absolute().create(mol);
			System.out.println("  " +  abs_products[i]);
		}
		
		//if (products.length > 1)
			//System.out.println("Line " + curLine + "  chemaxonProducts = " + t1ld.chemaxonProducts);
			//System.out.println("Line " + curLine + "  chemaxonProducts = " + products.length);
					
		
		List<String> result = applySmirks(t1ld.smirks, t1ld.kekulizedSubstrate);
		
		if (result == null)
		{
		}
		else
		{
			int nErr =  0;
			System.out.println("Result:");
			for (int i = 0; i < result.size(); i++)
			{	
				boolean FlagOK = false;
				for (int k = 0; k < abs_products.length; k++)
					if (result.get(i).equals(abs_products[k]))
					{
						FlagOK = true;
						break;
					}
				
				System.out.println("  " + result.get(i) + "   status = " + FlagOK);
				
				if (!FlagOK)
					nErr++;
			}
			
			if (nErr > 0)
			{
				nErrors++;
				System.out.println("Error on line " + curLine);
			}
		}
		
		return 0;
	}
	
	public List<String> applySmirks(String smrk, String smi)
	{
		try
		{
			SMIRKSManager smrkMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
			smrkMan.setFlagSSMode(SmartsConst.SSM_MODE.SSM_NON_IDENTICAL_FIRST);
			smrkMan.setFlagProcessResultStructures(true);
			smrkMan.setFlagClearHybridizationBeforeResultProcess(true);
			smrkMan.setFlagClearImplicitHAtomsBeforeResultProcess(true);
			smrkMan.setFlagClearAromaticityBeforeResultProcess(true);
			smrkMan.setFlagAddImplicitHAtomsOnResultProcess(true);
			smrkMan.setFlagConvertAddedImplicitHToExplicitOnResultProcess(false);
			smrkMan.setFlagConvertExplicitHToImplicitOnResultProcess(true);
			smrkMan.getSmartsParser().mSupportDoubleBondAromaticityNotSpecified = false;
			smrkMan.setFlagApplyStereoTransformation(true);

			SMIRKSReaction reaction = smrkMan.parse(smrk);
			if (!smrkMan.getErrors().equals(""))
				throw new RuntimeException("Invalid SMIRKS: " + smrkMan.getErrors());

			IAtomContainer target = smilesParser.parseSmiles(smi);
			
			for (IAtom atom : target.atoms())
				if (atom.getFlag(CDKConstants.ISAROMATIC))
					atom.setFlag(CDKConstants.ISAROMATIC, false);
			for (IBond bond : target.bonds())
				if (bond.getFlag(CDKConstants.ISAROMATIC))
					bond.setFlag(CDKConstants.ISAROMATIC, false);
			// do not add Hs: https://sourceforge.net/p/cdk/mailman/message/34608714/
			//			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(target);
			//			CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
			//			adder.addImplicitHydrogens(target);
			MoleculeTools.convertImplicitToExplicitHydrogens(target);

			// for our project, we want to use daylight aromaticity
			// CDKHueckelAromaticityDetector.detectAromaticity(target);
			Aromaticity aromaticity = new Aromaticity(ElectronDonation.daylight(),
					Cycles.or(Cycles.all(), Cycles.edgeShort()));
			aromaticity.apply(target);

			IAtomContainerSet resSet2 = smrkMan.applyTransformationWithSingleCopyForEachPos(target,
					null, reaction, SmartsConst.SSM_MODE.SSM_ALL);
			List<String> result = new ArrayList<String>();
			if (resSet2 != null)
				for (int i = 0; i < resSet2.getAtomContainerCount(); i++)
				{
					IAtomContainer mol = resSet2.getAtomContainer(i);
					AtomContainerManipulator.suppressHydrogens(mol);
					String smiles = SmilesGenerator.absolute().create(mol);
					result.add(smiles);
				}
			return result;
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			System.err.println("SMIRKS " + smrk);
			System.err.println("SMILES " + smi);
			//e.printStackTrace();
			return null;
		}
	}
	
	
	static class Task1LineData {
		public String error = null;
		public String smirksId = null;
		public String smirks = null;
		public String substrateId = null;
		public String substrate = null;
		public String kekulizedSubstrate = null;
		public String chemaxonProducts = null;
		public String ambitProducts = null;
		public String notIncludedAmbitProduct = null;
		
		public static Task1LineData parseLine(String line)
		{
			Task1LineData t1ld = new Task1LineData();
			String tokens[] = line.split("\t");
			if (tokens.length > 0)
				t1ld.error = tokens[0];
			if (tokens.length > 1)
				t1ld.smirksId = tokens[1];
			if (tokens.length > 2)
				t1ld.smirks = tokens[2];
			if (tokens.length > 3)
				t1ld.substrateId = tokens[3];
			if (tokens.length > 4)
				t1ld.substrate = tokens[4];
			if (tokens.length > 5)
				t1ld.kekulizedSubstrate = tokens[5];
			if (tokens.length > 6)
				t1ld.chemaxonProducts = tokens[6];
			if (tokens.length > 7)
				t1ld.ambitProducts = tokens[7];
			if (tokens.length > 8)
				t1ld.notIncludedAmbitProduct = tokens[8];
			
			return t1ld;
		};
		
	}

}
