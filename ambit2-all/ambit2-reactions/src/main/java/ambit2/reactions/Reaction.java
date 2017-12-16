package ambit2.reactions;


import org.openscience.cdk.interfaces.IAtomContainer;


public class Reaction 
{	
	protected boolean FlagUse = true;
	protected int id = 0;
	protected String name = null;
	protected String reactionClass = null;
	protected String info = null;
	protected IAtomContainer reactants = null;
	protected IAtomContainer products = null;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isFlagUse() {
		return FlagUse;
	}

	public void setFlagUse(boolean flagUse) {
		FlagUse = flagUse;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
		
	public String getReactionClass() {
		return reactionClass;
	}

	public void setReactionClass(String reactionClass) {
		this.reactionClass = reactionClass;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public IAtomContainer getReactants() {
		return reactants;
	}

	public void setReactants(IAtomContainer reactants) {
		this.reactants = reactants;
	}

	public IAtomContainer getProducts() {
		return products;
	}

	public void setProducts(IAtomContainer products) {
		this.products = products;
	}
		
}
