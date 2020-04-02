package ambit2.groupcontribution.groups;


public class DGroup extends ValueTransformedGroup 
{
	private String groupDesignation = "";
	private boolean missing = true;
		
	public void setGroupDesignation(String groupDesignation) {		
		this.groupDesignation = groupDesignation;
	}

	@Override
	public String getDesignation() {
		return groupDesignation;
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
		return Type.D_GROUP;
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

