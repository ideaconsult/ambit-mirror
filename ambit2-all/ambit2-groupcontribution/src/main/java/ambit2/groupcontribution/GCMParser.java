package ambit2.groupcontribution;

import java.util.ArrayList;
import java.util.List;

import ambit2.groupcontribution.correctionfactors.DescriptorInfo;
import ambit2.groupcontribution.correctionfactors.ICorrectionFactor;
import ambit2.groupcontribution.correctionfactors.SmartsCorrectionFactor;
import ambit2.groupcontribution.descriptors.ILocalDescriptor;
import ambit2.groupcontribution.descriptors.LocalDescriptorManager;
import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsParser;

public class GCMParser 
{
	public static String[] CorrectionFactorsDesignation = { "G", "AP" };
	
	public boolean FlagOmitEmptyTokens = true;
	public boolean FlagTrimTokens = true;
	
	private IsomorphismTester isoTester = null;
	private SmartsParser parser = null;
	
	private List<String> errors = new ArrayList<String>();
	
	public GCMParser() 
	{
	}
	
	public GCMParser(SmartsParser parser, IsomorphismTester isoTester) 
	{
		this.parser = parser;
		this.isoTester = isoTester;
	}
	
	public List<String> getErrors()
	{
		return errors;
	}
	
	public String getAllErrorsAsString()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < errors.size(); i++)
			sb.append(errors.get(i) + "\n");
		return sb.toString();
	}
	
	public List<ILocalDescriptor> getLocalDescriptorsFromString(String locDescr)
	{
		return getLocalDescriptorsFromString(locDescr, ",");
	}
	
	public List<ILocalDescriptor> getLocalDescriptorsFromString(String locDescr, String separator)
	{
		errors.clear();
		List<ILocalDescriptor> descriptors = new ArrayList<ILocalDescriptor>();
		String tokens[] = locDescr.split(separator);
		for (int i = 0; i < tokens.length; i++ )
		{
			String tok;
			if (FlagTrimTokens)
				tok = tokens[i].trim();
			else
				tok = tokens[i];
						
			if (tok.equals(""))
			{	
				if (!FlagOmitEmptyTokens)
					errors.add("Emtpy local descriptor string");
				continue;
			}			
			
			ILocalDescriptor d = getLocalDescriptor(tok);
			if (d != null)
				descriptors.add(d);
		}
		
		return descriptors;
	}
	
	ILocalDescriptor getLocalDescriptor(String descrStr) 
	{
		List<ILocalDescriptor> predefined = LocalDescriptorManager.getPredefinedLocalDescriptors();
		for (ILocalDescriptor d : predefined)
		{
			if (d.getShortName().equals(descrStr))
			{
				try {
					ILocalDescriptor newDescr = d.getClass().newInstance();
					return newDescr;
				}
				catch(Exception e){
					errors.add(e.getMessage());
				}
			}
		}
		
		//Check for non-predefined
		//TODO
		
		errors.add("Unknown local descriptor: " + descrStr);
		return null;
	}
	
	public List<DescriptorInfo> getGlobalDescriptorsFromString(String globDescr)
	{
		return getGlobalDescriptorsFromString(globDescr, ",");
	}
	
	public List<DescriptorInfo> getGlobalDescriptorsFromString(String globDescr, String separator)
	{
		errors.clear();
		List<DescriptorInfo> descriptors = new ArrayList<DescriptorInfo>();
		String tokens[] = globDescr.split(separator);
		for (int i = 0; i < tokens.length; i++ )
		{
			String tok;
			if (FlagTrimTokens)
				tok = tokens[i].trim();
			else
				tok = tokens[i];
						
			if (tok.equals(""))
			{	
				if (!FlagOmitEmptyTokens)
					errors.add("Emtpy global descriptor string");
				continue;
			}			
			
			DescriptorInfo di = new DescriptorInfo();
			di.setName(tok);
			descriptors.add(di);
		}
		
		return descriptors;
	}
	
	public List<ICorrectionFactor> getCorrectionFactorsFromString(String cfStr)
	{
		errors.clear();
		List<ICorrectionFactor> corFactors = new ArrayList<ICorrectionFactor>();
		int openBrackets = 0;
		List<Integer> sepPos = new ArrayList<Integer>();
		for (int i = 0; i < cfStr.length(); i++)
		{
			switch (cfStr.charAt(i))
			{
			case '(':
				openBrackets++;
				break;
			case ')':
				openBrackets--;
				break;
			case ',':
				if (openBrackets == 0)
					sepPos.add(i);
				break;
			}
		}
		
		int endPos = 0;
		int pos = 0;
		for (int i = 0; i < sepPos.size(); i++)
		{
			endPos = sepPos.get(i);
			String s =  cfStr.substring(pos, endPos);
			ICorrectionFactor cf = getCorrectionFactorFromString(s);
			if (cf != null)
				corFactors.add(cf);
			pos = endPos;
		}
		
		endPos = cfStr.length();
		String s =  cfStr.substring(pos, endPos);
		ICorrectionFactor cf = getCorrectionFactorFromString(s);
		if (cf != null)
			corFactors.add(cf);
		
		return corFactors;
	}
	
	ICorrectionFactor getCorrectionFactorFromString(String cfStr)
	{
		if (cfStr.startsWith("G("))
			return extractSmartsCorrectionFactor(cfStr);
		
		if (cfStr.startsWith("AP("))
			return extractAtomPairCorrectionFactor(cfStr);
				
		errors.add("Unknown correction factor: " + cfStr);
		return null;
	}
	
	ICorrectionFactor extractSmartsCorrectionFactor(String cfStr)
	{
		//Analyzing string: extract smarts argument
		int openBrackets = 1; //Counting opening "G("
		int closingArgBracketPos = -1;
		for (int i = 2; i < cfStr.length(); i++)
		{
			switch (cfStr.charAt(i))
			{
			case '(':
				openBrackets++;
				break;
			case ')':
				openBrackets--;
				if (openBrackets == 0)
				{	
					closingArgBracketPos = i;
				}	
				break;			
			}			
		}
		
		if (closingArgBracketPos == -1)
		{	
			errors.add("Incorrect correction factor: " + cfStr);
			return null;
		}
		
		
		String smarts = cfStr.substring(2, closingArgBracketPos);
		try {
			SmartsCorrectionFactor cf = new SmartsCorrectionFactor(smarts, parser, isoTester);
			if (cf.getError().equals(""))
				return cf;
			else
			{
				errors.add(cf.getError());
				return null;
			}
		}
		catch (Exception e)
		{
			errors.add("Correction factor error: " + e.getMessage());
			return null;
		}
	}
	
	ICorrectionFactor extractAtomPairCorrectionFactor(String cfStr)
	{
		//TODO
		return null;
	}
	
}
