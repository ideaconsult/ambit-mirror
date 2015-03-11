package ambit2.smarts;

public class SmartsFlags 
{
	//These are flags for SMARTS mapping
	public boolean mNeedNeighbourData;
	public boolean mNeedValenceData;
	public boolean mNeedRingData;    //data with ring sizes for each atom
	public boolean mNeedRingData2;	  //data with ring 'internal formal numbers' for each atom
	public boolean mNeedExplicitHData;
	public boolean mNeedParentMoleculeData;
	public boolean hasRecursiveSmarts;
	
	
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("mNeedNeighbourData = " + mNeedNeighbourData + "\n");
		sb.append("mNeedValenceData = " +mNeedValenceData + "\n");
		sb.append("mNeedRingData = " + mNeedRingData + "\n");
		sb.append("mNeedRingData2 = " + mNeedRingData2 + "\n");
		sb.append("mNeedExplicitHData = " + mNeedExplicitHData + "\n");
		sb.append("mNeedParentMoleculeData = " + mNeedParentMoleculeData + "\n");
		sb.append("hasRecursiveSmarts = " + hasRecursiveSmarts + "\n");
		
		return(sb.toString());
	}
}
