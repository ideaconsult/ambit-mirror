package ambit2.core.filter;

import org.openscience.cdk.interfaces.IAtomContainer;

public class RecordIntervalCondition implements IMoleculeFilterCondition
{
	private int firstValue = 1;
	private int lastValue = 1000000000;
	
	@Override
	public boolean useMolecule(IAtomContainer container, int recordNum) 
	{
		return (firstValue <= recordNum && recordNum <= lastValue);
	}

	public int getFirstValue() {
		return firstValue;
	}

	public void setFirstValue(int firstValue) {
		this.firstValue = firstValue;
	}

	public int getLastValue() {
		return lastValue;
	}

	public void setLastValue(int lastValue) {
		this.lastValue = lastValue;
	}
	
	public String toString()
	{
		if (firstValue == lastValue)
			return "#Mol="  + firstValue;
		
		return "#Mol=" +  MoleculeFilter.openBracket 
				+ ((firstValue == (-MoleculeFilter.BIG_INTEGER))?"":firstValue) 
				+ MoleculeFilter.intervalSep + ((lastValue == MoleculeFilter.BIG_INTEGER)?"":lastValue) 
				+ MoleculeFilter.closeBracket ; 
	}

}
