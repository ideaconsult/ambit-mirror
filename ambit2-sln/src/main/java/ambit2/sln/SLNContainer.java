package ambit2.sln;

import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

public class SLNContainer extends QueryAtomContainer
{
	static final long serialVersionUID = 345274336895563284L;
	
	private SLNDictionary localDictionary = null;
	private SLNDictionary globalDictionary = null;
	
	//Flags that determine what types of objects are stored in SLNContainer
	private boolean IsStructureOnly = false;
	
	
	
	public boolean getIsStructureOnly()
	{
		return IsStructureOnly;
	}
	
	public void setIsStructureOnly(boolean IsStructureOnly)
	{
		this.IsStructureOnly = IsStructureOnly;
	}
	
	public SLNDictionary getLocalDictionary()
	{
		return localDictionary;
	}
	
	public SLNDictionary getGlobalDictionary()
	{
		return globalDictionary;
	}
	
}
