package ambit2.groupcontribution.groups;


public class AtomGroup extends ValueTransformedGroup 
{	
	private String atomDesignation = "";
	private boolean missing = true;
	
	public void setAtomDesignation(String atomDesignation) {		
		this.atomDesignation = atomDesignation;
	}
	
	@Override
	public String getDesignation() {		
		return atomDesignation;
	}
	
	
	@Override
	public IGroupSet getGroupSet() {		
		return null;
	}

	@Override
	public GroupInfo getInfo() {		
		return null;
	}


	@Override
	public Type getType() {
		return Type.ATOM;
	}

	@Override
	public boolean isMissing() {
		return missing;
	}

	@Override
	public void setMissing(boolean missing) {
		this.missing = missing;
	}	

}
