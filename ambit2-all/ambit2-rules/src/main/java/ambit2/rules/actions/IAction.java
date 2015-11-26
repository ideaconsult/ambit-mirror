package ambit2.rules.actions;

public interface IAction 
{
	public String getName();
	public void doAction() throws Exception;
	public Object getResult();
	public void setTarget(Object target);
	public Object getTarget();
}
