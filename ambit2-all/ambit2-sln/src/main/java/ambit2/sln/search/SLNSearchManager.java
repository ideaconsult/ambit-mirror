package ambit2.sln.search;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.sln.SLNContainer;
import ambit2.sln.SLNParser;
import ambit2.smarts.IsomorphismTester;

public class SLNSearchManager 
{
	boolean FlagAutoResetErrors = true;
	
	
	IsomorphismTester isoTester = new IsomorphismTester();
	SLNParser slnParser = new SLNParser();
	List<String> errors = new ArrayList<String>();
	
	String slnQueryString = null;
	SLNContainer query = null;
	
	
	
	public void setQuery(String slnQuery)
	{
		if (FlagAutoResetErrors)
			reset();
		
		slnQueryString = slnQuery;
		query = slnParser.parse(slnQuery);
		
		if (!slnParser.getErrorMessages().equals(""))
		{
			errors.add(slnParser.getErrorMessages());			
			return;
		}
	}
	
	public void reset()
	{
		errors.clear();
	}
	
	public String getAllErrorsAsString()
	{
		//TODO
		return("");
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	
	
	public SLNContainer getQuerySLNContainer() {
		return query;
	}

	public void setQuerySLNContainer(SLNContainer query) {
		if (FlagAutoResetErrors)
			reset();
		this.query = query;
	}
	
		
	public boolean isFlagAutoResetErrors() {
		return FlagAutoResetErrors;
	}

	public void setFlagAutoResetErrors(boolean flagAutoResetErrors) {
		FlagAutoResetErrors = flagAutoResetErrors;
	}

	
	boolean mappingIn(IAtomContainer target) throws Exception
	{
		isoTester.setQuery(query);
		return (isoTester.hasIsomorphism(target));
	}
	
	
	
	
	
	
}
