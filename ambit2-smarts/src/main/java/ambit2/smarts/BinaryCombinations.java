package ambit2.smarts;


public class BinaryCombinations 
{
	int nDigits = 0;
	int curMaxDigit = 0;
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
		curComb = new int[n];
		curMaxDigit = 0;
		for (int i = 0; i < nDigits; i++)
			curComb[i] = 0;
		return curComb;
	}
	
	public boolean hasNext()
	{
		return (curMaxDigit < nDigits);
	}
	
	public int[] next()
	{	
		int n = 0;
		boolean FlagNextDigit = true;
		
		while ((n < nDigits) && FlagNextDigit)
		{
			FlagNextDigit = addToDigit(n);
			n++;
		}
		
		if (curMaxDigit < n)
			curMaxDigit = n;
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
