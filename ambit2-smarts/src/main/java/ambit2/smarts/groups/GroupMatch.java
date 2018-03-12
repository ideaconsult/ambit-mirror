package ambit2.smarts.groups;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.QuerySequenceElement;
import ambit2.smarts.SmartsParser;

public class GroupMatch 
{
	IsomorphismTester isoTester = null;
	SmartsParser parser = null;
	String smarts = null;
	IQueryAtomContainer smartsQuery = null;
	List<QuerySequenceElement> sequence = null;
	String error = null;
	
	public GroupMatch(String smarts, SmartsParser parser, IsomorphismTester isoTester)
	{
		this.smarts = smarts;
		this.parser = parser;
		this.isoTester = isoTester;
		configure();
	}
	
	public void configure()
	{
		smartsQuery = parser.parse(smarts);
		
		if (parser.getErrors().isEmpty())
		{	
			// parser.setNeededDataFlags();  //TODO 
			isoTester.setQuery(smartsQuery);
			sequence = isoTester.transferSequenceToOwner();
		}
		else
			error = parser.getErrorMessages();
	}
	
	
	public boolean match(IAtomContainer target)
	{
		/*
		SmartsParser.prepareTargetForSMARTSSearch(
				flags.mNeedNeighbourData, 
				flags.mNeedValenceData, 
				flags.mNeedRingData, 
				flags.mNeedRingData2, 
				flags.mNeedExplicitHData , 
				flags.mNeedParentMoleculeData, mol);	
			*/	
		
		isoTester.setSequence(smartsQuery, sequence);
		return isoTester.hasIsomorphism(target);
	}
	
	public String getError()
	{
		return error;
	}
}
