package ambit2.sln.dictionary;

import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.sln.SLNAtom;
import ambit2.sln.SLNBond;
import ambit2.sln.SLNContainer;


public abstract class GenericDictionaryObject implements ISLNDictionaryObject
{
	String name = null;
	String sln = null;
	SLNAtom atom = null;
	SLNBond bond = null;
	SLNContainer container = null;
	
	
	@Override
	public String getObjectName() {
		return name;
	}
	
	@Override
	public String getSLNString() {
		return sln;
	}

	@Override
	public SLNContainer getSLNContainer() {		
		return container;
	}
	
	@Override
	public SLNAtom getSLNAtom() {		
		return atom;
	}

	@Override
	public SLNBond getSLNBond() {
		return bond;
	}
	
	void makeContainerFromAtom() {
		 container = new SLNContainer(SilentChemObjectBuilder.getInstance());
		 container.addAtom(atom);
	}
}
