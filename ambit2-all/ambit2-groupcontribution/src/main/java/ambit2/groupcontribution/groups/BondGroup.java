package ambit2.groupcontribution.groups;



public class BondGroup extends ValueTransformedGroup 
{
	private String bondDesignation = "";
	private double contribution = 0.0;
	private boolean missing = true;
	
	@Override
	public double getContribution() {
		return contribution;
	}

	@Override
	public void setContribution(double contribution) {
		this.contribution = contribution;
	}
	
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
