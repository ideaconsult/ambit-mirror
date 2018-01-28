package ambit2.reactions.retrosynth;



public interface IReactionSequenceHandler 
{
	public static class RSEvent {
		public EventType type = null;
		public int level = 0;
		public Object params[] = null;
	}
	
	public static enum EventType {
		ADD_MOLECULE, 
		REMOVE_MOLECULE, 
		ADD_LEVEL, 
		REMOVE_LEVEL,
		ADD_MANY_LEVELS,
		RESET
	}
	
	public ReactionSequence getReactionSequence();
	
	public void setReactionSequence(ReactionSequence sequence);
	
	public void registerEvent(RSEvent event);
	
}
