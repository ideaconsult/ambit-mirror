package ambit2.groupcontribution.groups;


public class AtomGroup implements IGroup 
{	

	@Override
	public String getDesignation() {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public IGroupSet getGroupSet() {		
		return null;
	}


	@Override
	public double getContribution() {
		
		return 0;
	}


	@Override
	public GroupInfo getInfo() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Type getType() {
		return Type.ATOM;
	}

}
