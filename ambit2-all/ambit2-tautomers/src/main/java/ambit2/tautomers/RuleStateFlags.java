package ambit2.tautomers;

public class RuleStateFlags 
{
	//These are flags for SMARTS mapping
	public boolean mNeedNeighbourData;
	public boolean mNeedValenceData;
	public boolean mNeedRingData;    //data with ring sizes for each atom
	public boolean mNeedRingData2;	  //data with ring 'internal formal numbers' for each atom
	public boolean mNeedExplicitHData;
	public boolean mNeedParentMoleculeData;
	public boolean hasRecursiveSmarts;
}
