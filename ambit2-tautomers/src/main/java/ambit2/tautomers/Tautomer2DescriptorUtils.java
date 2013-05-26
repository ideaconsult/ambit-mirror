package ambit2.tautomers;

import java.util.Vector;
import java.io.File;
import java.io.RandomAccessFile;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import ambit2.smarts.SmartsHelper;


public class Tautomer2DescriptorUtils 
{
	TautomerManager tman = new TautomerManager();	
	String endLine = System.getProperty("line.separator");
	
	public void generateFulltautomerInfo(String smilesFile, String outFileName) throws Exception
	{	
		RandomAccessFile outFile = new RandomAccessFile(outFileName,"rw");
		outFile.setLength(0);
		
		RandomAccessFile inFile = new RandomAccessFile(smilesFile,"r");			
		long length = inFile.length();
		
		int n = 0;		
		while (inFile.getFilePointer() < length)
		{	
			n++;
			String line = inFile.readLine();
			tautomerFullInfo(n, line.trim(), outFile);
		}
		
		outFile.close();
		inFile.close();
	}
	
	public int tautomerFullInfo(int strNum, String targetSmiles, RandomAccessFile outFile)
	{	
		try
		{
			IMolecule mol = null;
			SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
			mol = sp.parseSmiles(targetSmiles);
			
			tman.setStructure(mol);
			Vector<IAtomContainer> resultTautomers = tman.generateTautomersIncrementaly();
			
			outFile.write(("" + strNum + "   " + targetSmiles + "  " + resultTautomers.size() + "  "  +  endLine).getBytes());
			System.out.println("    -->  " + resultTautomers.size() + " tautomers");
			
			if (resultTautomers.size() > 1)  
				for (int i = 0; i < resultTautomers.size(); i++)
				{
					IAtomContainer tautomer = resultTautomers.get(i);
					double rank = ((Double)tautomer.getProperty("TAUTOMER_RANK")).doubleValue();
					String smiles = SmartsHelper.moleculeToSMILES(tautomer).trim();
					outFile.write(("" + strNum + "   " + smiles + "  " + rank  +  endLine).getBytes());
				}
			
		}	
		catch(Exception e){
			System.out.println(e.toString());
		}
		
		return 0;
	}
	
	
	public void analyseTautomerDescriptors(String descrFile) throws Exception
	{
		RandomAccessFile inFile = new RandomAccessFile(descrFile,"r");			
		long length = inFile.length();
		
		int n = 0;		
		while (inFile.getFilePointer() < length)
		{	
			n++;
			String line = inFile.readLine();
			processLine(line, n);
		}
		
		inFile.close();
	}
	
	void processLine(String line, int lineNum)
	{
		//TODO
	}
	
}
