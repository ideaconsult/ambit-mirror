package ambit2.groupcontribution.groups;


public class AtomGroup implements IGroup 
{	
	private String atomDesignation = "";
	private double contribution = 0.0;
	
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
	public double getContribution() {
		return contribution;
	}
	
	@Override
	public void setContribution(double contribution) {
		this.contribution = contribution;
	}


	@Override
	public GroupInfo getInfo() {		
		return null;
	}


	@Override
	public Type getType() {
		return Type.ATOM;
	}	

}
