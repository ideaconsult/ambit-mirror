package ambit2.smarts.groups;

import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.QuerySequenceElement;
import ambit2.smarts.SmartsFlags;
import ambit2.smarts.SmartsParser;

public class GroupMatch 
{
	IsomorphismTester isoTester = null;
	SmartsParser parser = null;
	String smarts = null;
	IQueryAtomContainer smartsQuery = null;
	List<QuerySequenceElement> sequence = null;
	List<IAtom> reursiveAtoms = null;
	String error = null;
	
	SmartsFlags flags = new SmartsFlags();
	
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
			parser.setNeededDataFlags(); 
			flags.hasRecursiveSmarts = parser.hasRecursiveSmarts;
	    	flags.mNeedExplicitHData = parser.needExplicitHData();
	    	flags.mNeedNeighbourData = parser.needNeighbourData();
	    	flags.mNeedParentMoleculeData = parser.needParentMoleculeData();
	    	flags.mNeedRingData = parser.needRingData();
	    	flags.mNeedRingData2 = parser.needRingData2();
	    	flags.mNeedValenceData = parser.needValencyData();
			
	    	//recursiveAtoms = getRecursiveAtoms(smartsQuery);
	    	
			isoTester.setQuery(smartsQuery);
			sequence = isoTester.transferSequenceToOwner();
		}
		else
			error = parser.getErrorMessages();
	}
	
	
	public boolean match(IAtomContainer target)
	{	
		SmartsParser.prepareTargetForSMARTSSearch(flags, target);
    	
		//if (flags.hasRecursiveSmarts)
    	 //   mapRecursiveAtomsAgainstTarget(recursiveAtoms, target);
		
		isoTester.setSequence(smartsQuery, sequence);
		return isoTester.hasIsomorphism(target);
	}
	
	public String getError()
	{
		return error;
	}
}
