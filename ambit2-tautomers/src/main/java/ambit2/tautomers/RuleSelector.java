package ambit2.tautomers;

public class RuleSelector 
{
	protected int selectionMode = TautomerConst.RSM_RANDOM;
	
	
	
	public int getSelectionMode(){
		return selectionMode;
	}
	
	public void setSelectionMode(int selectionMode){
		this.selectionMode = selectionMode;
	}
	
	public static RuleSelector getDefaultRandomSelector()
	{
		RuleSelector selector = new RuleSelector();  
		selector.setSelectionMode(TautomerConst.RSM_RANDOM);
		return selector;
	}
	
	
	public static RuleSelector getDefaultInteligentSelector()
	{
		RuleSelector selector = new RuleSelector();  
		selector.setSelectionMode(TautomerConst.RSM_INTELIGENT);
		//TODO
		return selector;
	}
}
