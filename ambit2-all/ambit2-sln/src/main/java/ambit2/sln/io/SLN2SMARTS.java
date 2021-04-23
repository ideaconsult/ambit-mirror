package ambit2.sln.io;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.sln.SLNContainer;
import ambit2.sln.SLNHelper;
import ambit2.sln.SLNParser;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;
/**
 * 
 * @author nick
 * Conversion from SLN notation to SMILES/SMARTS/SMIRKS notations
 */
public class SLN2SMARTS 
{	
	private List<String> conversionErrors = new ArrayList<String>();
	private List<String> conversionWarnings = new ArrayList<String>();
	
	private SLNParser slnParser = new SLNParser();
	private SmartsParser smartsParser = new SmartsParser();
	private SLN2ChemObject slnConverter = new SLN2ChemObject();
	private SLNHelper slnHelper = new SLNHelper();
	private SmartsHelper smartsHelper = new SmartsHelper(SilentChemObjectBuilder.getInstance()); 
	
	public List<String> getConversionErrors() {
		return conversionErrors;
	}
	
	public List<String> getConversionWarnings() {
		return conversionWarnings;
	}
	
	public SLN2ChemObjectConfig getConversionConfig() {
		return slnConverter.getConversionConfig();
	}
	
	public String getAllErrors()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < conversionErrors.size(); i++)
			sb.append(conversionErrors.get(i) + "\n");
		return sb.toString();
	}
	
	public boolean hasErrors() {
		return (conversionErrors.size() > 0);
	}
	
	protected void reset() {
		conversionErrors.clear();
		conversionWarnings.clear();
	}
	
	public String slnToSmiles(String sln) throws Exception
	{
		reset();		
		SLNContainer container = slnParser.parse(sln);
		String parserErr = slnParser.getErrorMessages(); 
		if (!parserErr.equals(""))
		{
			conversionErrors.add(parserErr);			
			return null;
		}
		
		IAtomContainer mol = slnConverter.slnContainerToAtomContainer(container);
		String smiles = SmartsHelper.moleculeToSMILES(mol, false);
		conversionErrors.addAll(slnConverter.getConversionErrors());
		conversionWarnings.addAll(slnConverter.getConversionWarnings());
		
		return smiles;
	}
	
	public String slnToSmarts(String sln)
	{
		reset();		
		SLNContainer container = slnParser.parse(sln);
		String parserErr = slnParser.getErrorMessages(); 
		if (!parserErr.equals(""))
		{
			conversionErrors.add(parserErr);			
			return null;
		}
		
		IQueryAtomContainer query = slnConverter.slnContainerToQueryAtomContainer(container);
		String smarts = smartsHelper.toSmarts(query);
		conversionErrors.addAll(slnConverter.getConversionErrors());
		conversionWarnings.addAll(slnConverter.getConversionWarnings());
		
		return smarts;
	}
	
	public String slnToSmirks(String sln)
	{
		//TODO
		return null;
	}
	
	public String smilesToSLN(String sln)
	{
		//TODO
		return null;
	}
	
	public String smartsToSLN(String smarts)
	{
		IQueryAtomContainer query = smartsParser.parse(smarts);		
		String parserErr = smartsParser.getErrorMessages();
		if (!parserErr.equals(""))
		{			
			conversionErrors.add(parserErr);	
			return null;
		}
		
		SLNContainer slnCon = slnConverter.QueryAtomContainerToSLNContainer(query);		
		conversionErrors.addAll(slnConverter.getConversionErrors());
		conversionWarnings.addAll(slnConverter.getConversionWarnings());
		
		if (slnCon == null)			
			return null;
		else
			return slnHelper.toSLN(slnCon);
	}
	
	public String smirksToSLN(String sln)
	{
		//TODO
		return null;
	} 
	
}
