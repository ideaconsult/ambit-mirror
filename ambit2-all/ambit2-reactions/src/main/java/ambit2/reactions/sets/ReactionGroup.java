package ambit2.reactions.sets;

import java.util.ArrayList;

public class ReactionGroup 
{
	private String name = "";
	private ArrayList<ReactionData> reactions = new ArrayList<ReactionData>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<ReactionData> getReactions() {
		return reactions;
	}
	public void setReactions(ArrayList<ReactionData> reactions) {
		this.reactions = reactions;
	}
	
	public void addNewReaction(ReactionData rData)
	{
		reactions.add(rData);
	}
}
