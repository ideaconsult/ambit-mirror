/*
Copyright (C) 2007-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.smarts;

import java.util.Stack;

/**
 * 
 * @author Nikolay Kochev nick@uni-plovdiv.bg
 */
public class SmartsLogicalExpression 
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
		if (SmartsConst.priority[top][op] < 0)
			 return(false);
		
		operations.pop();
		switch (top)
		{
			case SmartsConst.LO_NOT:
				doNOT();
			break;
			
			case SmartsConst.LO_AND:
			case SmartsConst.LO_ANDLO:	
				doAND();
			break;
			
			case SmartsConst.LO_OR:
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
			case SmartsConst.LO_NOT:
				doNOT();
			break;
			
			case SmartsConst.LO_AND:
			case SmartsConst.LO_ANDLO:	
				doAND();
			break;
			
			case SmartsConst.LO_OR:
				doOR();
			break;
		}
	}
}
