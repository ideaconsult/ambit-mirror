package ambit2.reactions.sets;

import java.util.ArrayList;

public class ReactionSet 
{	
	
	
	private ArrayList<ReactionData> reactions = new ArrayList<ReactionData>();
	private ArrayList<ReactionGroup> reactionGroups = new ArrayList<ReactionGroup>();

	public ArrayList<ReactionData> getReactions() {
		return reactions;
	}

	public void setReactions(ArrayList<ReactionData> reactions) {
		this.reactions = reactions;
	}

	public ArrayList<ReactionGroup> getReactionGroups() {
		return reactionGroups;
	}

	public void setReactionGroups(ArrayList<ReactionGroup> reactionGroups) {
		this.reactionGroups = reactionGroups;
	}
	
	
}
