package ambit2.smarts;

public class ChiralPermutations 
{
	public static final int basic4Permutation[] = new int[] {0,1,2,3};
	
	/**
	 * Calculates the number of pair switches needed to obtain
	 * permutation2 starting from permutation1
	 * The number is not unique (since various paths are possible),
	 * but the parity (even/odd) of the switches number must always be the same
	 * 
	 * @param permutation1
	 * @param permutation2
	 * @return number of pair switches
	 */
	public static int getNumOfPairSwitches(int permutation1[], int permutation2[])
	{
		int n = 0;
		int perm[] = permutation1.clone();  //a temporary state
		
		//System.out.println(permutationToString(perm));
		
		for (int i = 0; i < permutation1.length -1; i++)
		{
			int pos = getPos(permutation2[i], perm);
			//moving from position 'pos' to position i
			if (pos != i)
			{	
				move(pos, i, perm);
				n += (pos - i);
				//System.out.println("moving pos " + pos + " --> to pos " + i + "   " + permutationToString(perm));
			}			
		}
		
		
		return n;
	}
	
	public static void switchPos(int pos1, int pos2, int array[])
	{
		int temp = array[pos1];
		array[pos1] = array[pos2];
		array[pos2] = temp;
	}
	
	public static int getPos(int value, int array[])
	{
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	public static void move(int startPos, int endPos, int array[])
	{	
		int step = 1;
		if (startPos > endPos)
			step = -1;
		
		int nSteps = Math.abs(endPos-startPos); 
		int pos = startPos; 
		
		for (int i = 0; i < nSteps; i++)
		{	
			switchPos(pos, pos + step, array);
			pos+= step;
		}
	}
	
	public static String permutationToString(int p[])
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < p.length; i++) 
			sb.append(" " + p[i]);
		return sb.toString();
	}
	
}
