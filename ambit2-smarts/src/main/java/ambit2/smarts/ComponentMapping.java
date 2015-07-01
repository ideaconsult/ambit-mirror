package ambit2.smarts;

import java.util.Stack;

public class ComponentMapping 
{
	public boolean fragMaps[][] = null;
	public int components[] = null;
	
	public boolean checkComponentMapings()
	{
		//Combination is defined as an array a[] where:
		//i-th fragment is mapped onto target component a[i]
		//In order to return 'true' there must be found a combination
		//where all elements are different from -1 (i.e. each fragment is mapped onto a target component)
		//First depth-search algorithm is applied to search such a combination.
		Stack<int[]> comb = new Stack<int[]>();
		
		//Generation of the initial combinations
		for (int k = 0; k < fragMaps[0].length; k++)
		{
			if (!fragMaps[0][k])
				continue;
			int a[] = new int[components.length];
			resetCombination(a);
			a[0] = k;
			if (setComponentInCombination(0,a) != null)
				comb.push(a);
		}					
		boolean compMapResult = false;
		
		//Tree search of the mapping combination (depth-first search)
		while ((!comb.empty()) && (!compMapResult))
		{
			int a[] = comb.pop();
			compMapResult = addComponentToCombination(a, comb);
		}
		
		return(compMapResult);
	}
	
	protected void resetCombination(int a[])
	{
		for (int i = 0; i < a.length; i++)
			a[i] = -1;
	}
	
	protected boolean addComponentToCombination(int a[], Stack<int[]> comb)
	{
		int n = firstElementInCombination(-1,a);
		if (n == -1)
			return(true); //No elements equal to -1 were found i.e. all fragments are mapped
		
		for (int k = 0; k < fragMaps[n].length; k++)
		{
			if (!fragMaps[n][k])
				continue;
			int newC[] = a.clone();			
			newC[n] = k;
			if (setComponentInCombination(n,newC) != null)
				comb.push(newC);
		}
		
		return(false);
	}
	
	protected int[] setComponentInCombination(int n, int a[])
	{
		//All fragments that are in the component of n-fragment
		//must to be mapped onto target component a[n] as well
		//If NOT, null pointer  is returned		
		for (int i = n+1; i < components.length; i++)
		{	
			if(components[i] == components[n])
			{	
				if (fragMaps[i][a[n]]) //i-th fragment maps target component a[n]
					a[i] = a[n];
				else
					return(null);
			}
			else
				break; //It is applied because the fragments for a given component are sequential 
		}	
		
		return (a);
	}
	
	protected int firstElementInCombination(int el, int a[])
	{
		for (int i =0; i < a.length; i++)
			if (a[i] == el)
				return(i);
		return(-1);
	}
	
}
