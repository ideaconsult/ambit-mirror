package ambit2.reactions.sets;

import java.util.ArrayList;

public class ReactionSet 
{	
	
	private String name = null;
	private String info = null;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 * @param rData
	 * This function analyses rData object and creates group or fill group info if needed
	 * Also reaction data is added into the proper group and default reaction set.
	 */
	public void addNewReaction(ReactionData rData)
	{	
		if (rData.getSmirks() == null) //rdo object contains info for a set or a group within a set
		{	
			if (rData.getGroup() == null)
			{
				info = rData.getInfo();
			}
			else
			{
				//This is info for an existing group or new group is created
				ReactionGroup group = findReactionGroup(rData.getGroup());
				if (group == null)
				{
					group = new ReactionGroup();
					reactionGroups.add(group);
				}
				
				group.setInfo(rData.getInfo());
			}
		}
		else //Adding new reaction
		{
			if (rData.getGroup() == null)
				reactions.add(rData);
			else
			{
				ReactionGroup group = findReactionGroup(rData.getGroup());
				if (group == null)
				{
					group = new ReactionGroup();
					group.setName(rData.getGroup());
					reactionGroups.add(group);
				}
				group.addNewReaction(rData);
			}
		}
		
	}
	
	private ReactionGroup findReactionGroup(String groupName)
	{
		for (ReactionGroup group : reactionGroups)
			if (group.getName().equals(groupName))
				return group;
		return null;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	
}
