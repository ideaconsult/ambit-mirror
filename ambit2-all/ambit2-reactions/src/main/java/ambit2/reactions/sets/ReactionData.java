package ambit2.reactions.sets;



public class ReactionData 
{
	private String smirks = null;
	private String name = null;
	private String type = null;
	private String info = null;
	private String group = null;
	private String subGroup = null;
	private String subSubGroup = null;
	//private HashMap<String,String> properties = null;
	
	@Override
	public String toString()
	{
		return name;
	}

	public String getSmirks() {
		return smirks;
	}

	public void setSmirks(String smirks) {
		this.smirks = smirks;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getSubGroup() {
		return subGroup;
	}

	public void setSubGroup(String subGroup) {
		this.subGroup = subGroup;
	}

	public String getSubSubGroup() {
		return subSubGroup;
	}

	public void setSubSubGroup(String subSubGroup) {
		this.subSubGroup = subSubGroup;
	}
}
