package ambit2.groupcontribution.groups;



public class BondGroup extends ValueTransformedGroup 
{
	private String bondDesignation = "";
	private boolean missing = true;
	
	public void setBondDesignation(String bondDesignation) {		
		this.bondDesignation = bondDesignation;
	}
	
	
	@Override
	public String getDesignation() {
		return bondDesignation;
	}

	@Override
	public GroupInfo getInfo() {
		return null;
	}

	@Override
	public IGroupSet getGroupSet() {
		return null;
	}

	@Override
	public Type getType() {
		return Type.BOND;
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
