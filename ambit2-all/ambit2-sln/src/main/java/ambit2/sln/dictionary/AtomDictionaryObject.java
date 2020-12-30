package ambit2.sln.dictionary;

import ambit2.sln.SLNAtom;
import ambit2.sln.SLNBond;
import ambit2.sln.SLNContainer;

public class AtomDictionaryObject implements ISLNDictionaryObject
{
	String sln = null;
	SLNAtom atom = null;
	
	@Override
	public Type getObjectType() {
		return Type.ATOM;
	}

	@Override
	public String getSLNString() {
		return sln;
	}

	@Override
	public SLNContainer getSLNContainer() {		
		return null;
	}

	
	@Override
	public SLNAtom getSLNAtom() {
		
		return atom;
	}

	@Override
	public SLNBond getSLNBond() {
		return null;
	}
	
}
