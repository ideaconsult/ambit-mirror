package ambit2.smarts;


public class BinaryCombinations 
{
	int nDigits = 0;
	int curDigit = 0;
	int curComb[] = null;
	
	int c1[][] = {
			{0}, 
			{1}
	};

	int c2[][] = {	
			{0,0}, 
			{0,1},
			{1,0},
			{1,1} 
	};
	
	
	public int[] getFirst(int n)
	{
		nDigits = n;
		curComb = new int[n];
		curDigit = 0;
		for (int i = 0; i < nDigits; i++)
			curComb[i] = 0;
		return curComb;
	}
	
	
	
	
}
