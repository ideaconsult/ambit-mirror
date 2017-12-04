package ambit2.tautomers.rules;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsParser;
import ambit2.tautomers.IRuleInstance;
import ambit2.tautomers.RuleStateFlags;

public class TautomerRegion 
{
	boolean useRegion = false;	
	boolean excludeNitroGroup = false;
	boolean excludeNitroGroupPartially = false;
	boolean excludeNitroxides = false;
	boolean excludeSulfonylGroups = false;
	boolean excludeAzideGroups = false;
	boolean excludeAromaticSystems = false;	
	List<String> customExcludeRegionSmarts = new ArrayList<String>();
	
	List<CustomTautomerRegion> customExcludeRegions = new ArrayList<CustomTautomerRegion>();
	//List<Integer> includeAtomIndices = null;
	List<Integer> excludeAtomIndices = new ArrayList<Integer>();
	List<String> errors = new ArrayList<String>();
	IsomorphismTester isoTester = null;
	
		
	public boolean useRegion() {
		return useRegion;	
	}
	
	public void nullifyRegion()
	{
		excludeNitroGroup = false;
		excludeNitroGroupPartially = false;
		excludeNitroxides = false;	
		excludeAromaticSystems = false;
		
		customExcludeRegionSmarts.clear();
		customExcludeRegions.clear();
		excludeAtomIndices.clear();
		errors.clear();
	}

	public void setUseRegion(boolean useRegion) {
		this.useRegion = useRegion;
	}
	
	public boolean isexcludeNitroGroupPartially() {
		return excludeNitroGroupPartially;
	}

	public void setExcludeNitroGroupPartially(boolean excludeNitroGroupPartially) {
		this.excludeNitroGroupPartially = excludeNitroGroupPartially;
	}

	public boolean isExcludeNitroxides() {
		return excludeNitroxides;
	}

	public void setExcludeNitroxides(boolean excludeNitroxides) {
		this.excludeNitroxides = excludeNitroxides;
	}

	/*
	public List<Integer> getIncludeAtomIndices() {
		return includeAtomIndices;
	}

	public void setIncludeAtomIndices(List<Integer> includeAtomIndices) {
		this.includeAtomIndices = includeAtomIndices;
	}
	*/
	
	public boolean isExcludeNitroGroup() {
		return excludeNitroGroup;
	}


	public void setExcludeNitroGroup(boolean excludeNitroGroup) {
		this.excludeNitroGroup = excludeNitroGroup;
	}


	public boolean isExcludeSulfonylGroups() {
		return excludeSulfonylGroups;
	}

	public void setExcludeSulfonylGroups(boolean excludeSulfonylGroups) {
		this.excludeSulfonylGroups = excludeSulfonylGroups;
	}

	public boolean isExcludeAzideGroups() {
		return excludeAzideGroups;
	}

	public void setExcludeAzideGroups(boolean excludeAzideGroups) {
		this.excludeAzideGroups = excludeAzideGroups;
	}

	public boolean isExcludeAromaticSystems() {
		return excludeAromaticSystems;
	}


	public void setExcludeAromaticSystems(boolean excludeAromaticSystems) {
		this.excludeAromaticSystems = excludeAromaticSystems;
	}
	
	public List<String> getCustomExcludeRegionSmarts() {
		return customExcludeRegionSmarts;
	}

	public void setCustomExcludeRegionsSmarts(List<String> customExcludeRegionSmarts) {
		this.customExcludeRegionSmarts = customExcludeRegionSmarts;
		setCustomExcludeRegions();
	}
	
	public List<Integer> getExcludeAtomIndices() {
		return excludeAtomIndices;
	}

	public void setExcludeAtomIndices(List<Integer> excludeAtomIndices) {
		this.excludeAtomIndices = excludeAtomIndices;
	}
	
	void setCustomExcludeRegions()
	{
		customExcludeRegions.clear();
		
		SmartsParser sp = new SmartsParser();
		sp.mSupportDoubleBondAromaticityNotSpecified = true;
		
		for (int i = 0; i<customExcludeRegionSmarts.size(); i++)
		{
			IQueryAtomContainer q = sp.parse(customExcludeRegionSmarts.get(i));			
			String errorMsg = sp.getErrorMessages();
			if (!errorMsg.equals(""))
			{	
				errors.add(errorMsg);
			}
			else
			{	
				sp.setNeededDataFlags();	
				RuleStateFlags flags = new RuleStateFlags();
				flags.hasRecursiveSmarts = sp.hasRecursiveSmarts;
				flags.mNeedExplicitHData = sp.needExplicitHData();
				flags.mNeedNeighbourData = sp.needNeighbourData();
				flags.mNeedParentMoleculeData = sp.needParentMoleculeData();
				flags.mNeedRingData = sp.needRingData();
				flags.mNeedRingData2 = sp.needRingData2();
				flags.mNeedValenceData = sp.needValencyData();
				
				CustomTautomerRegion ctr = new CustomTautomerRegion();
				ctr.flags = flags;
				ctr.query = q;
				ctr.smarts = customExcludeRegionSmarts.get(i);
				customExcludeRegions.add(ctr);
			}
		}
		
		//isoTester is set when custom smarts regions are used
		if (isoTester == null)
			isoTester = new IsomorphismTester();
	}
	
	public boolean isRuleInstanceInRegion(IRuleInstance ruleInst, IAtomContainer mol)
	{
		/*
		//Include mode takes precedence over exclude mode
		if (includeAtomIndices != null)
		{
			for (IAtom at : ruleInst.atoms)
			{	
				int atInd = mol.getAtomNumber(at);
				if (includeAtomIndices.contains(atInd))
					continue;
				else
					return false; //rule instance atom is not within include list
			}
			return true;
		}
		*/
		
		//Handle exclude atoms region
		for (int i = 0; i < excludeAtomIndices.size(); i++)
		{
			IAtom exclAt = mol.getAtom(excludeAtomIndices.get(i));
			if (ruleInst.getAtoms().contains(exclAt))
				return false; //rule instance contains an 'exclude' atom
		}
		return true;
	}
	
	public void calculateRegion(IAtomContainer target)
	{
		excludeAtomIndices.clear();
		
		if (excludeNitroGroup)
		{
			//Flag excludeNitroGroup takes precedence 
			//over flag excludeNitroGroupPartially
			List<IAtom[]> pos = CustomTautomerRegion.getNitroGroupPositions(target);
			addPositionsToExcludeRegion(pos, target);
		}
		else
			if (excludeNitroGroupPartially)
			{
				List<IAtom[]> pos = CustomTautomerRegion.getNitroGroupPositions(target);
				for (int i = 0; i < pos.size(); i++)
				{
					IAtom[] atoms = pos.get(i);
					for (int k = 0; k < atoms.length; k++)
					{
						//only one of the double bonds is allowed to tautomerized						
						//TODO	
					}	
				}
			}
		
		if (excludeNitroxides)
		{
			List<IAtom[]> pos = CustomTautomerRegion.getNitroxidePositions(target);
			addPositionsToExcludeRegion(pos, target);
		}
		
		if (excludeSulfonylGroups)
		{
			List<IAtom[]> pos = CustomTautomerRegion.getSulfonylGroupPositions(target);
			addPositionsToExcludeRegion(pos, target);
		}
		
		if (excludeAzideGroups)
		{
			List<IAtom[]> pos = CustomTautomerRegion.getAzideGroupPositions(target);
			addPositionsToExcludeRegion(pos, target);
		}
		
		if (excludeAromaticSystems)
		{
			int n = target.getAtomCount();
			for (int i = 0; i < n; i++)
			{
				IAtom atom = target.getAtom(i);
				if (atom.getFlag(CDKConstants.ISAROMATIC))
					if (!isIndexInList(i, excludeAtomIndices))
						excludeAtomIndices.add(i);
			}	
		}
		
		if (!customExcludeRegions.isEmpty())		
			for (int i = 0; i < customExcludeRegions.size(); i++)
			{
				try
				{
					List<List<IAtom>> maps = calcCustomRegionMaps(customExcludeRegions.get(i), target);
					addMapsToExcludeRegion(maps, target);
				}
				catch(Exception e)
				{}
			}
	}
	
	List<List<IAtom>> calcCustomRegionMaps(CustomTautomerRegion customReg, IAtomContainer target) throws Exception
	{				
		SmartsParser.prepareTargetForSMARTSSearch(
				customReg.flags.mNeedNeighbourData, 
				customReg.flags.mNeedValenceData, 
				customReg.flags.mNeedRingData, 
				customReg.flags.mNeedRingData2, 
				customReg.flags.mNeedExplicitHData , 
				customReg.flags.mNeedParentMoleculeData, target);	
		
		isoTester.setQuery(customReg.query);
		List<List<IAtom>> maps = isoTester.getAllIsomorphismMappings(target);
		return maps;
	}
	
	void addMapsToExcludeRegion(List<List<IAtom>> maps, IAtomContainer target)
	{			
		for (List<IAtom> map : maps)
		{	
			for (IAtom at: map)
			{
				int atNum = target.getAtomNumber(at);
				if (atNum != -1)
				{	
					if (!isIndexInList(atNum, excludeAtomIndices))
						excludeAtomIndices.add(atNum);
				}	
			}	
		}
	}
	
	void addPositionsToExcludeRegion(List<IAtom[]> groupPositions, IAtomContainer target)
	{	
		for (int i = 0; i < groupPositions.size(); i++)
		{
			IAtom[] atoms = groupPositions.get(i);
			for (int k = 0; k < atoms.length; k++)
			{
				int atNum = target.getAtomNumber(atoms[k]);
				if (atNum != -1)
				{	
					if (!isIndexInList(atNum, excludeAtomIndices))
						excludeAtomIndices.add(atNum);
				}	
			}	
		}
	}
	
	boolean isIndexInList(int index, List<Integer> list)
	{
		for (Integer i : list )
			if (index == i.intValue())
				return true;
		return false;
	}
	
}
