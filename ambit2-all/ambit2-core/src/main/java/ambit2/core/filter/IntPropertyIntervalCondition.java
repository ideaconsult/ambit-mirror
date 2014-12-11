package ambit2.core.filter;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class IntPropertyIntervalCondition implements IMoleculeFilterCondition
{
	public static enum PropertyType{
		NA, NB, CYCLOMATIC, NAromAt
	}
	
	private int firstValue = 0;
	private int lastValue = 0;
	private PropertyType propertyType = PropertyType.NA;
	
	@Override
	public boolean useMolecule(IAtomContainer container, int recordNum) 
	{
		if (container == null)
			return false;
		double prop = getProperty(container);
		return (firstValue <= prop && prop <= lastValue);
	}
	
	int getProperty(IAtomContainer mol){
		switch (propertyType)
		{
		case NA:
			return mol.getAtomCount();
		case NB:
			return mol.getBondCount();
		case CYCLOMATIC:
			return mol.getBondCount() - mol.getAtomCount() + 1;	
		case NAromAt:
			return getNumAromAtoms(mol);
		}
		return 0;
	}

	public double getFirstValue() {
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

	public PropertyType getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(PropertyType propertyType) {
		this.propertyType = propertyType;
	}
	
	public String toString()
	{
		if (firstValue == lastValue)
			return "" + propertyType + "="  + firstValue;
		
		return "" + propertyType + "=" + MoleculeFilter.openBracket 
				+ ((firstValue == (-MoleculeFilter.BIG_INTEGER))?"":firstValue) 
				+ MoleculeFilter.intervalSep + ((lastValue == MoleculeFilter.BIG_INTEGER)?"":lastValue)
				+ MoleculeFilter.closeBracket ; 
	}
	
	int getNumAromAtoms(IAtomContainer mol)
	{
		int n = 0;
		for (IAtom at : mol.atoms())
			if (at.getFlag(CDKConstants.ISAROMATIC))
				n++;
		return n;
	}
	
}