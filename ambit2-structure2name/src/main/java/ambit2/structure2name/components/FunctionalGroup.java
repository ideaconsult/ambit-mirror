package ambit2.structure2name.components;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;


public class FunctionalGroup implements IIUPACComponent 
{

	@Override
	public CompType getType() {
		return CompType.FUNCTIONAL_GROUP;
	}

	@Override
	public long getRank() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setRank(long rank) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IAtomContainer getSubstructure() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getIUPACAtomNumbering() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getDoubleBondIndices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getTripleBondIndices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getHeteroAtomIndices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ComponentConnection> getConnections() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMainToken() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
