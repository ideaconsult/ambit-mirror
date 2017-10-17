package ambit2.groupcontribution.groups;

public class BondGroup extends ValueTransformedGroup 
{
	private boolean missing = true;
	
	@Override
	public double getContribution() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setContribution(double contribution) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDesignation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GroupInfo getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IGroupSet getGroupSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
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
