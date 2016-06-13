package ambit2.smarts.test;

import java.io.File;
import java.io.RandomAccessFile;



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
	
	public static void main(String[] args)
	{
		AutomaticSmirksTestUtilities ast = new AutomaticSmirksTestUtilities();
		ast.inFileName = "/Volumes/Data/Projects/AmbitSMIRKS2016/stereo-errors__.txt";
		ast.startLine = 1;
		ast.maxNumOfProcessLines = 10;
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
		switch (task)
		{
		case task1:
			processLine1(line);
			break;
		}
		return 0;
	}
	
	int processLine1(String line)
	{
		System.out.println("Line" + curLine + "  " + line);
		return 0;
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
