package ambit2.mopac.test;

import java.io.FileWriter;
import java.io.RandomAccessFile;

import ambit2.mopac.MopacUtilities;

public class MopacUtilitiesTest 
{
	static MopacUtilities utils;  
	
	public static void main(String[] args) throws Exception 
	{
		utils = new MopacUtilities();
		
		//test("C=C");
		//test("CCCC=C");
		
		calcStrPairEnergies("D:/Projects/Nina/Tautomers/test-analysis/pairs-rule1-keto_enol.csv",
				"D:/Projects/Nina/Tautomers/test-analysis/energies-test.csv", 10);
	}
	
	static void test(String smiles) throws Exception
	{
		double energy = utils.getTotalEnergy(smiles);
		System.out.println("MOPAC energy for " + smiles + " is " + energy);
	}
	
	
	/*
	 * Expected csv file where the first two columns are the SMILES notations for the pair
	 */
	static void calcStrPairEnergies(String inputFile, String outputFile, int startLine)
	{
		RandomAccessFile reader = null;
		FileWriter writer = null;
		
		try
		{
			try {
				writer = new FileWriter(outputFile);
				
			}catch (Exception x) {
				//in case smth's wrong with the writer file, close it and throw an error
				try {writer.close(); } catch (Exception xx) {}
				throw x;
			}
			
			
			reader = new RandomAccessFile(inputFile,"r");			
			long length = reader.length();
			
			int n = 0;
			while (reader.getFilePointer() < length)
			{	
				n++;
				String line = reader.readLine();
				if (n < startLine)
				{	
					System.out.println("Skipping: #" + n);
					continue;
				}
				
				System.out.println("#" + n);
				String tok[] =  line.split(",");
				if (tok.length < 2)
				{	
					System.out.println("too few tokens in line " + n);
					continue;
				}
				
				try
				{
					double e0 = utils.getTotalEnergy(tok[0]);
					double e1 = utils.getTotalEnergy(tok[1]);
					String newLine = "" + e0 + "," + e1 + "," + (e1-e0) + "," + line;
					writer.write(newLine + "\n");
					System.out.println(newLine);
					writer.flush();
				}	
				catch (Exception x)
				{	
					System.out.println("Error for line: " + n + " " + line + "\n" + x.getMessage());
				}
			}
		}
		catch (Exception x)
		{	
		} 
		finally {
			try { reader.close(); } catch (Exception x) {};
			try { writer.close(); } catch (Exception x) {};
		};
	}

}
