package ambit2.sln.io;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

import ambit2.sln.SLNContainer;
import ambit2.smarts.SMIRKSReaction;

public class SLN2ChemObject 
{
	private List<String> conversionErrors = new ArrayList<String>();
	private List<String> conversionWarnings = new ArrayList<String>();

	public List<String> getConversionErrors() {
		return conversionErrors;
	}
	
	public List<String> getConversionWarnings() {
		return conversionWarnings;
	}
	
	public String getAllErrors()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < conversionErrors.size(); i++)
			sb.append(conversionErrors.get(i) + "\n");
		return sb.toString();
	}
	
	public LinearNotationType getCompatibleNotation(SLNContainer slnContainer)	{
		//TODO
		return null;
	}
	
	public SLNContainer atomContainerToSLNContainer(AtomContainer container)
	{
		//TODO
		return null;
	}
	
	public AtomContainer  slnContainerToAtomContainer(SLNContainer container)
	{
		//TODO
		return null;
	}
	
	public IQueryAtomContainer slnContainerToQueryAtomContainer(SLNContainer container)
	{
		//TODO
		return null;
	}
	
	public  SLNContainer QueryAtomContainerToSLNContainer(IQueryAtomContainer query)
	{
		//TODO
		return null;
	}
	
	public SMIRKSReaction slnContainerToSMIRKSReaction(SLNContainer container)
	{
		//TODO
		return null;
	}
	
	public  SLNContainer SMIRKSReactionToSLNContainer(SMIRKSReaction reaction)
	{
		//TODO
		return null;
	}
}
