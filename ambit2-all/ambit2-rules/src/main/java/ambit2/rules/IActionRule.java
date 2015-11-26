package ambit2.rules;

import ambit2.rules.actions.IAction;

public interface IActionRule extends IBasicRule 
{
	public IAction getAction();	
	public void apply(Object target) throws Exception;
}
