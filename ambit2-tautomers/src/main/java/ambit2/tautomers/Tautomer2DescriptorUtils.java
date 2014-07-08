package ambit2.tautomers;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Vector;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import ambit2.smarts.SmartsHelper;


public class Tautomer2DescriptorUtils 
{
	TautomerManager tman = new TautomerManager();	
	String endLineSymb = System.getProperty("line.separator");
	RandomAccessFile outFile = null;
	
	//work variables
	public int startLineNum = 0;
	public int endLineNum = 1000000000;
	public String splitter = ",";  
	int descrStartColumn = -1;
	int strNumColumn = -1;
	
	
	int curLine; 
		
	public void generateFulltautomerInfo(String smilesFile, String outFileName) throws Exception
	{	
		openOutputFile(outFileName);
		
		RandomAccessFile inFile = new RandomAccessFile(smilesFile,"r");			
		long length = inFile.length();
		
		int n = 0;		
		while (inFile.getFilePointer() < length)
		{	
			n++;
			curLine = n;
			String line = inFile.readLine();
			
			if (n < startLineNum)
				continue;
			
			if (n > endLineNum)
				break;
			
			tautomerFullInfo(n, line.trim());
		}
		
		closeOutputFile();
		inFile.close();
	}
	
	public int tautomerFullInfo(int strNum, String targetSmiles)
	{	
		try
		{	
			IMolecule mol = null;
			SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
			mol = sp.parseSmiles(targetSmiles);
			
			tman.setStructure(mol);
			Vector<IAtomContainer> resultTautomers = tman.generateTautomersIncrementaly();
			
			output("" + strNum + "   " + targetSmiles + "  " + resultTautomers.size() + "  "  +  endLineSymb);
			System.out.println("    -->  " + resultTautomers.size() + " tautomers");
			
			if (resultTautomers.size() > 1)  
				for (int i = 0; i < resultTautomers.size(); i++)
				{
					IAtomContainer tautomer = resultTautomers.get(i);
					double rank = ((Double)tautomer.getProperty("TAUTOMER_RANK")).doubleValue();
					String smiles = SmartsHelper.moleculeToSMILES(tautomer,false).trim();
					output("" + strNum + "   " + smiles + "  " + rank  +  endLineSymb);
				}
		}	
		catch(Exception e){
			System.out.println(e.toString());
		}
		
		return 0;
	}
	
	
	public void analyseTautomerDescriptors(String descrFile, String outFileName, 
					int descrStartColumn, int strNumColumn) throws Exception
	{
		this.descrStartColumn = descrStartColumn; 
		this.strNumColumn = strNumColumn;  
		
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
		String tokens[] = line.split(splitter);
	}
	
	
	//----------- some file utilities ------------------------------
	
	
	void openOutputFile(String outFileName) throws Exception
	{
		File file = new File(outFileName);
		outFile = new RandomAccessFile(file,"rw");
		outFile.setLength(0);
	}
	
	void closeOutputFile() throws Exception 
	{	
		if (outFile != null)
			outFile.close();
	}
	
	int output(String data)
	{
		try
		{
			outFile.write(data.getBytes());
		}
		catch (Exception e)
		{
			System.out.println("output error: " + e.toString());
			return(-1);
		}
		return(0);
	}
	
}
