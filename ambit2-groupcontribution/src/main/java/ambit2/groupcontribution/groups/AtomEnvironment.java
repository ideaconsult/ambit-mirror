package ambit2.groupcontribution.groups;


public class AtomEnvironment implements IGroup 
{
	private Type envType = Type.ATOM_TOP_LAYER_1;
	private String envDesignation = "";
	private double contribution = 0.0;
	
	public void setEnvironmentDesignation(String envDesignation) {		
		this.envDesignation = envDesignation;
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
	public String getDesignation() {		
		return envDesignation;
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
		return envType;
	}	

	public void setEnviromentType(Type envType) {
		this.envType = envType;
	}

}
