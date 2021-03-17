package ambit2.groupcontribution.nmr.nmr_1h;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ambit2.groupcontribution.nmr.Substituent;
import ambit2.groupcontribution.nmr.nmr_1h.HAtomEnvironment.ShiftAssociation;


public class HNMRPredefinedKnowledgeBase 
{
	public static final String keyWordPrefix = "$$";
	public static final String keyWordSeparator = "=";
	
	public static final String ALKANES_CH3[] = 
	{
		"$$NAME= ALKANES/CH3",
		"$$SMARTS= [CH3]",
		"$$INFO= ",
		"$$BASIC_SHIFT= 0.83",
		"$$SHIFT_DESIGNATIONS= Za Zb",
		//"$$SUBSTITUENT_POS_ATOM_INDICES= 1 1",
		"$$POSITION_DISTANCES = 1 2",
		
		"$$SUBST= -C C 0.00 0.05",
		"$$SUBST= -C=C C=[CH2] 0.85 0.20",
		
	};
	
	
	public static HNMRKnowledgeBase getHNMRKnowledgeBase()
	{
		HNMRKnowledgeBase knowledgeBase = new HNMRKnowledgeBase();
		
		HAtomEnvironment haEnv = parseHAtomEnvironment (ALKANES_CH3, "Alkanes/CH3", knowledgeBase.errors);
		knowledgeBase.hAtomEnvironments.add(haEnv);
		
		return knowledgeBase;
	}
	
	public static HNMRKnowledgeBase getHNMRKnowledgeBase(File file) throws Exception
	{
		HNMRKnowledgeBase knowledgeBase = new HNMRKnowledgeBase();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			try {
				HAtomEnvironment haEnv = null;
				String errorPrefix0 = null;
				int nHAEnv = 0;
				int nLine = 0;
				String line;
				
				while ( (line = br.readLine()) != null ) 
				{
					nLine++;
					if (line.isEmpty())
						continue;
					if (line.charAt(0) == '#')
						continue;
					
					if (line.contains("$$H_ATOM_ENVIRONMENT"))
					{
						if (haEnv != null)
						{	
							//Register HAtomEnvironment
							postProcess (haEnv, 
									errorPrefix0 + ((haEnv.name != null)?(" " + haEnv.name + ": "):": "), 
									knowledgeBase.errors);
							knowledgeBase.hAtomEnvironments.add(haEnv);
						}
						
						//Start new HAtomEnvironment
						haEnv = new HAtomEnvironment();
						nHAEnv++;
						errorPrefix0 = "HAtomEnvironment #" + nHAEnv;
					}
					else
					{
						if (haEnv == null)
						{	
							//This is a file line outside H_ATOM_ENVIRONMENT sections
							//(before, between or after H_ATOM_ENVIRONMENT sections)S
							//It is an empty line or a line with JJ coupling rules
							if (line.contains("$$2JJ"))
								parseRule2JLine(line, "", knowledgeBase);
							
							if (line.contains("$$3JJ"))
								parseRule2JLine(line, "", knowledgeBase);
							
							continue; 
						}	
						else
						{	
							parseLine(line, haEnv, 
									errorPrefix0 + ((haEnv.name != null)?(" " + haEnv.name):"") + ": line " + nLine, 
									knowledgeBase.errors);
						}	
					}
				}
				
				if (haEnv != null)
				{	
					//Register last HAtomEnvironment
					postProcess (haEnv, errorPrefix0, knowledgeBase.errors);
					knowledgeBase.hAtomEnvironments.add(haEnv);
				}
				
				br.close();
			}
			catch (IOException e) {
				throw e;
			}
		}
		catch (FileNotFoundException e) {
			throw e;
		}


		return knowledgeBase;
	}
	
	
	public static HAtomEnvironment parseHAtomEnvironment(String lines[], String errorPrefix, List<String> errors)
	{
		HAtomEnvironment haEnv = new HAtomEnvironment();
		
		for (int i = 0; i < lines.length; i++)
		{
			parseLine(lines[i], haEnv, errorPrefix + " line " + (i+1), errors);
		}
		
		postProcess (haEnv, errorPrefix, errors);
		return haEnv;
	}
	
	
	public static int parseLine(String line, HAtomEnvironment haEnv, String errorPrefix, List<String> errors)
	{
		int res = line.indexOf(keyWordPrefix, 0);
		int curPos = res;
		
		while (res != -1)
		{	
			res = line.indexOf(keyWordPrefix, curPos + keyWordPrefix.length());
			String keyword;
			if (res == -1)
				keyword = line.substring(curPos);
			else
			{	
				keyword = line.substring(curPos,res);
				curPos = res;	
			}
			
			parseKeyWord(keyword, haEnv, errorPrefix, errors);
		}	
		return 0;
	}
	
	
	public static void parseKeyWord(String keyword, HAtomEnvironment haEnv, String errorPrefix, List<String> errors)
	{
		int sepPos = keyword.indexOf(keyWordSeparator);		
		if (sepPos == -1)
		{	
			errors.add(errorPrefix + " Incorrect key word syntax: " + keyword);
			return;
		}
		
		String key = keyword.substring(keyWordPrefix.length(), sepPos).trim();		
		String keyValue = keyword.substring(sepPos+1).trim();
		
		if (key.equals("FLAG_USE"))	
		{	
			Boolean b = getBooleanFromString(keyValue);
			if (b == null)
				errors.add(errorPrefix + " incorrect FLAG_USE: " + keyValue);
			else
			{	
				haEnv.flagUse = b;
				haEnv.flagUseString = keyValue;
			}	
			return;
		}
				
		if (key.equals("NAME"))	
		{	
			haEnv.name = keyValue;
			return;
		}
		
		if (key.equals("SMARTS"))	
		{	
			haEnv.smarts = keyValue;
			return;
		}
		
		if (key.equals("INFO"))	
		{	
			haEnv.info = keyValue;
			return;
		}
		
		if (key.equals("BASIC_SHIFT"))	
		{	
			try {
				haEnv.chemShift0 = Double.parseDouble(keyValue);
			}
			catch(Exception e) {
				errors.add(errorPrefix + " incorrect BASIC_SHIFT: " + e.getMessage());
			}
			return;
		}
		
		if (key.equals("IMPLICIT_H_ATOMS_NUMBER"))	
		{	
			try {
				haEnv.implicitHAtomsNumber = Integer.parseInt(keyValue);
			}
			catch(Exception e) {
				errors.add(errorPrefix + " incorrect IMPLICIT_H_ATOMS_NUMBER: " + e.getMessage());
			}
			return;
		}
		
		if (key.equals("SHIFT_DESIGNATIONS"))	
		{	
			String tokens[] = keyValue.split("\\s+");
			haEnv.shiftDesignations = tokens;
			return;
		}
		
				
		if (key.equals("SUBSTITUENT_POS_ATOM_INDICES"))	
		{	
			String tokens[] = keyValue.split("\\s+");
			haEnv.substituentPosAtomIndices = new int[tokens.length];
			for (int i = 0; i < tokens.length; i++)
			{
				try {
					int ival = Integer.parseInt(tokens[i]);
					haEnv.substituentPosAtomIndices[i] = ival;
				}
				catch (Exception e) {
					errors.add(errorPrefix + " incorrect SUBSTITUENT_POS_ATOM_INDICES[" + (i+1) + "] : " + e.getMessage());
				}
			}
			return;
		}
		
		if (key.equals("H_POS_ATOM_INDICES"))	
		{	
			String tokens[] = keyValue.split("\\s+");
			haEnv.hPosAtomIndices = new int[tokens.length];
			for (int i = 0; i < tokens.length; i++)
			{
				try {
					int ival = Integer.parseInt(tokens[i]);
					haEnv.hPosAtomIndices[i] = ival;
				}
				catch (Exception e) {
					errors.add(errorPrefix + " incorrect H_POS_ATOM_INDICES[" + (i+1) + "] : " + e.getMessage());
				}
			}
			return;
		}
		
		if (key.equals("POSITION_DISTANCES"))	
		{	
			String tokens[] = keyValue.split("\\s+");
			haEnv.positionDistances = new int[tokens.length];
			for (int i = 0; i < tokens.length; i++)
			{
				try {
					int ival = Integer.parseInt(tokens[i]);
					haEnv.positionDistances[i] = ival;
				}
				catch (Exception e) {
					errors.add(errorPrefix + " incorrect POSITION_DISTANCES[" + (i+1) + "] : " + e.getMessage());
				}
			}
			return;
		}
		
		if (key.equals("HIGHER_PRIORITY_ENVIRONMENTS"))
		{	
			String tokens[] = keyValue.split("\\s+");
			haEnv.higherPriorityEnvironments = tokens;
			return;
		}
		
		if (key.equals("SHIFT_ASSOCIATION"))	
		{	
			if (keyValue.equals("SUBSTITUENT_POSITION"))
				haEnv.shiftsAssociation = ShiftAssociation.SUBSTITUENT_POSITION;
			else
				if (keyValue.equals("H_ATOM_POSITION"))
					haEnv.shiftsAssociation = ShiftAssociation.H_ATOM_POSITION;
				else
					errors.add(errorPrefix + " incorrect SHIFT_ASSOCIATION: " + keyValue);
			return;
		}
		
		if (key.equals("SUBST"))	
		{	
			String tokens[] = keyValue.split("\\s+");
			Substituent subst = new Substituent();
			if (tokens.length > 0)
				subst.name = tokens[0];
			if (tokens.length > 1)
				subst.smarts = tokens[1];
			if (tokens.length > 2)
			{	
				subst.chemShifts = new double[tokens.length-2];
				for (int i = 2; i < tokens.length; i++)
				{
					try {
						double val = Double.parseDouble(tokens[i]);
						subst.chemShifts[i-2] = val;
					}
					catch (Exception e) {
						errors.add(errorPrefix + " incorrect SUBSTITUENT chemical shift[" + (i-2+1) + "] : " + e.getMessage());
					}
				}
			}
			
		
			if (subst.name == null || subst.name.equals(""))
			{	
				errors.add(errorPrefix + " incorrect SUBSTITUENT: NAME (token #1) is an empty string");
				return;
			}
			
			if (subst.smarts == null || subst.smarts.equals(""))
			{	
				errors.add(errorPrefix + " incorrect SUBSTITUENT: SMARTS (token #2) is an empty string");
				return;
			}
			
			haEnv.substituents.add(subst);
			return;
		}
		
		if (key.equals("H_ATOM_ENVIRONMENT"))	
		{	
			//do nothing here
			return;
		}
		
		errors.add(errorPrefix + " Unknow key word: " + key);
	}
	
	public static void postProcess(HAtomEnvironment haEnv, String errorPrefix, List<String> errors)
	{
		if (haEnv.name == null)
			errors.add(errorPrefix + " NAME is missing");
		if (haEnv.smarts == null)
			errors.add(errorPrefix + " SMARTS is missing");
		if (haEnv.chemShift0 == null)
			errors.add(errorPrefix + " BASIC_SHIFT is missing");
		if (haEnv.shiftsAssociation == null)
			errors.add(errorPrefix + " SHIFT_ASSOCIATION is missing");
		
		//TODO: check number of atom pos indices and postion distances
		// check substituents
	}
	
	public static int parseRule2JLine(String line, String errorPrefix, HNMRKnowledgeBase knowledgeBase)
	{
		//TODO
		return 0;
	}
	
	public static int parseRule3JLine(String line, String errorPrefix, HNMRKnowledgeBase knowledgeBase)
	{
		//TODO
		return 0;
	}
	
	static Boolean getBooleanFromString(String s)
	{
		if (s.equalsIgnoreCase("true"))
			return true;
		if (s.equalsIgnoreCase("yes"))
			return true;
		if (s.equals("1"))
			return true;
		
		if (s.equalsIgnoreCase("false"))
			return false;
		if (s.equalsIgnoreCase("no"))
			return false;
		if (s.equals("0"))
			return false;
		
		return null; //Incorrect string
	}
	
	
}	