package ambit2.smarts;

public class SmartsFlags 
{
	//These are flags for SMARTS mapping
	boolean mNeedNeighbourData;
	boolean mNeedValenceData;
	boolean mNeedRingData;    //data with ring sizes for each atom
	boolean mNeedRingData2;	  //data with ring 'internal formal numbers' for each atom
	boolean mNeedExplicitHData;
	boolean mNeedParentMoleculeData;
	boolean hasRecursiveSmarts;
}
