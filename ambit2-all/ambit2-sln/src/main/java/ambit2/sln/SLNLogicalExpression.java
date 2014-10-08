package ambit2.sln;

import java.util.Stack;


public class SLNLogicalExpression 
{
	Stack<Integer> operations = new Stack<Integer>();
	Stack<Boolean> arguments = new Stack<Boolean>();
	
	public boolean getValue()
	{
		while (!operations.empty())
				doStackOperation();
		
		return(arguments.pop().booleanValue());
	}
	
	public void addLogOperation(int op)
	{
		boolean StackHighPriority = true;
		while (!operations.empty() && StackHighPriority)
		{
			StackHighPriority = doPriorityOperation(op);
		}
		
		operations.push(new Integer(op));
	}
	
	public void addArgument(boolean arg)
	{	
		arguments.push(new Boolean(arg));
	}
	
	void doNOT()
	{
		boolean arg = arguments.pop().booleanValue();
		arguments.push(new Boolean(!arg));
	}
	
	void doAND()
	{	
		boolean arg1 = arguments.pop().booleanValue();
		boolean arg2 = arguments.pop().booleanValue();
		arguments.push(new Boolean(arg1&&arg2));
	}
	
	void doOR()
	{	
		boolean arg1 = arguments.pop().booleanValue();
		boolean arg2 = arguments.pop().booleanValue();
		arguments.push(new Boolean(arg1||arg2));
	}
	
	boolean doPriorityOperation(int op)
	{	 
		int top = operations.peek().intValue();
		if (SLNConst.priority[top][op] < 0)
			 return(false);
		
		operations.pop();
		switch (top)
		{
			case SLNConst.LO_NOT:
				doNOT();
			break;
			
			case SLNConst.LO_AND:
			case SLNConst.LO_ANDLO:	
				doAND();
			break;
			
			case SLNConst.LO_OR:
				doOR();
			break;
		}
		return(true);
	}
	
	void doStackOperation()
	{	 
		int top = operations.pop().intValue();
		switch (top)
		{
			case SLNConst.LO_NOT:
				doNOT();
			break;
			
			case SLNConst.LO_AND:
			case SLNConst.LO_ANDLO:	
				doAND();
			break;
			
			case SLNConst.LO_OR:
				doOR();
			break;
		}
	}
}
