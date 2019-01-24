package ambit2.structure2name.components;

import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public interface IIUPACComponent 
{
	public static enum CompType {
		CHAIN, CYCLE, FUNCTIONAL_GROUP, ACYCLIC_COMPONENT, CYCLIC_COMPONENT
	}
	
	public CompType getType();
	public long getRank();
	public void setRank(long rank);
	public List<IAtom> getAtoms();
	
	//public IAtomContainer getSubstructure();
	//public List<Integer> getIUPACAtomNumbering();
	//public List<Integer> getDoubleBondIndices();
	//public List<Integer> getTripleBondIndices();
	//public List<Integer> getHeteroAtomIndices();
	//public List<ComponentConnection> getConnections();
	//public String getMainToken();
	
}
