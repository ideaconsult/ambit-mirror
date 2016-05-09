package ambit2.rules.actions;

public interface IAction<RESULT> 
{
	public String getName();
	public void doAction() throws Exception;
	public RESULT getResult();
	public void setTarget(Object target);
	public Object getTarget();
}
