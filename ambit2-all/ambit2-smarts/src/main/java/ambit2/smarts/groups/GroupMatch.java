package ambit2.smarts.groups;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.QuerySequenceElement;
import ambit2.smarts.SmartsAtomExpression;
import ambit2.smarts.SmartsConst;
import ambit2.smarts.SmartsFlags;
import ambit2.smarts.SmartsParser;
import ambit2.smarts.SmartsConst.SSM_MODE;

public class GroupMatch 
{
	private IsomorphismTester isoTester = null;
	private SmartsParser parser = null;
	private String smarts = null;
	private IQueryAtomContainer smartsQuery = null;
	private List<QuerySequenceElement> sequence = null;
	private List<SmartsAtomExpression> recursiveAtoms = null;
	private String error = "";
	
	private SmartsFlags flags = new SmartsFlags();
	private SSM_MODE FlagSSMode = SmartsConst.SSM_MODE.SSM_NON_IDENTICAL;
	private boolean FlagPrepareTarget = true;
	
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
			
	    	if (flags.hasRecursiveSmarts)
	    		recursiveAtoms = getRecursiveAtoms(smartsQuery);
	    	
			isoTester.setQuery(smartsQuery);
			sequence = isoTester.transferSequenceToOwner();
		}
		else
			error = parser.getErrorMessages();
	}
	
	public List<SmartsAtomExpression> getRecursiveAtoms(IQueryAtomContainer query) 
	{
		List<SmartsAtomExpression> recAtoms = new ArrayList<SmartsAtomExpression>();
		for (int i = 0; i < query.getAtomCount(); i++) {
			if (query.getAtom(i) instanceof SmartsAtomExpression) {
				SmartsAtomExpression sa = (SmartsAtomExpression) query.getAtom(i);
				if (sa.recSmartsStrings.size() > 0) {
					recAtoms.add(sa);
				}
			}
		}
		return recAtoms;
	}
	
	
	public boolean match(IAtomContainer target)
	{	
		if (FlagPrepareTarget)
			SmartsParser.prepareTargetForSMARTSSearch(flags, target);
    	
		if (flags.hasRecursiveSmarts)
			 mapRecursiveAtomsAgainstTarget(recursiveAtoms, target);
		
		isoTester.setSequence(smartsQuery, sequence);
		return isoTester.hasIsomorphism(target);
	}
	
	public boolean matchAtPosition(IAtomContainer target, int atomNum)
	{
		if (FlagPrepareTarget)
			SmartsParser.prepareTargetForSMARTSSearch(flags, target);
    	
		if (flags.hasRecursiveSmarts)
			 mapRecursiveAtomsAgainstTarget(recursiveAtoms, target);
		
		isoTester.setSequence(smartsQuery, sequence);
		return isoTester.checkIsomorphismAtPosition(target, atomNum);
	}
		
	public List<List<IAtom>> getMappings(IAtomContainer target)
	{
		if (FlagPrepareTarget)
			SmartsParser.prepareTargetForSMARTSSearch(flags, target);
    	
		if (flags.hasRecursiveSmarts)
			 mapRecursiveAtomsAgainstTarget(recursiveAtoms, target);
		
		isoTester.setSequence(smartsQuery, sequence);
		
		if (FlagSSMode == SmartsConst.SSM_MODE.SSM_NON_IDENTICAL) 
    	{
			List<List<IAtom>> maps = isoTester.getNonIdenticalMappings(target);
    		return maps;
    	}
		
		if (FlagSSMode == SmartsConst.SSM_MODE.SSM_NON_OVERLAPPING) 
    	{
			List<List<IAtom>> maps = isoTester.getNonOverlappingMappings(target);
    		return maps;
    	}
		
		if (FlagSSMode == SmartsConst.SSM_MODE.SSM_ALL) 
    	{
			List<List<IAtom>> maps = isoTester.getAllIsomorphismMappings(target);
    		return maps;
    	}
		
		return null;
	}
	
	
	public int matchCount(IAtomContainer target)
	{
		List<List<IAtom>> maps = getMappings(target);
		if (maps == null)
			return 0;
		else
			return maps.size();
	}
	
	public void mapRecursiveAtomsAgainstTarget(List<SmartsAtomExpression> recursiveAtoms, IAtomContainer target) {
		// Reset for new mapping
		for (int i = 0; i < recursiveAtoms.size(); i++)
			recursiveAtoms.get(i).recSmartsMatches = new ArrayList<List<IAtom>>();

		// The mapping info is stored "inside" each recursive atom
		List<IQueryAtomContainer> vRecCon;
		for (int i = 0; i < recursiveAtoms.size(); i++) {
			vRecCon = recursiveAtoms.get(i).recSmartsContainers;
			for (int j = 0; j < vRecCon.size(); j++) {
				isoTester.setQuery(vRecCon.get(j));
				List<Integer> pos = isoTester.getIsomorphismPositions(target);
				List<IAtom> v = new ArrayList<IAtom>();

				for (int k = 0; k < pos.size(); k++)
					v.add(target.getAtom(pos.get(k).intValue()));

				recursiveAtoms.get(i).recSmartsMatches.add(v);
			}
		}
	}
		
	public SSM_MODE getFlagSSMode() {
		return FlagSSMode;
	}

	public void setFlagSSMode(SSM_MODE flagSSMode) {
		FlagSSMode = flagSSMode;
	}	
	
	public boolean isFlagPrepareTarget() {
		return FlagPrepareTarget;
	}

	public void setFlagPrepareTarget(boolean flagPrepareTarget) {
		FlagPrepareTarget = flagPrepareTarget;
	}

	public IsomorphismTester getIsoTester() {
		return isoTester;
	}

	public SmartsParser getParser() {
		return parser;
	}

	public String getSmarts() {
		return smarts;
	}

	public IQueryAtomContainer getSmartsQuery() {
		return smartsQuery;
	}

	public List<QuerySequenceElement> getSequence() {
		return sequence;
	}

	public String getError()
	{
		return error;
	}
}
