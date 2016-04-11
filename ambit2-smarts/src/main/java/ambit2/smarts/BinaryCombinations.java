package ambit2.smarts;


public class BinaryCombinations 
{
	int nDigits = 0;
	int numOfComb = 0;
	int generatedCombinations = 0;
	int curComb[] = null;
	
	public static int c1[][] = {
			{0}, 
			{1}
	};

	public static int c2[][] = {	
			{0,0}, 
			{0,1},
			{1,0},
			{1,1} 
	};
	
	public static int c3[][] = {	
		{0,0,0},
		{0,0,1},
		{0,1,0},
		{0,1,1},
		{1,0,0},
		{1,0,1},
		{1,1,0},
		{1,1,1},
	};	
		 
	
	public int[] getFirst(int n)
	{
		nDigits = n;
		generatedCombinations = 1;
		curComb = new int[n];
		numOfComb = 1;
		for (int i = 0; i < nDigits; i++)
		{	
			curComb[i] = 0;
			numOfComb *= 2;
		}	
		return curComb;
	}
	
	public boolean hasNext()
	{
		return (generatedCombinations  < numOfComb);
	}
	
	public int[] next()
	{	
		int n = 0;
		boolean FlagNextDigit = true;
		
		while (FlagNextDigit && (n < nDigits))
		{
			FlagNextDigit = addToDigit(n);
			if (FlagNextDigit)
				n++;
		}
		
		generatedCombinations++;
		
		return curComb;
	}
	
	
	boolean addToDigit(int digNum)
	{
		curComb[digNum]++;
		if (curComb[digNum] == 2)
		{
			curComb[digNum]=0;
			return true;
		}
		return false;
	}
	
}
