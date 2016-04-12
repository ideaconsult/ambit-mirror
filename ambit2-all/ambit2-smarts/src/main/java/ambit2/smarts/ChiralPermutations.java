package ambit2.smarts;

public class ChiralPermutations 
{
	
	/**
	 * Calculates the number of pair switches needed to obtain
	 * permutation2 starting from permutation1
	 * The number is not unique (since various paths are possible),
	 * but the parity (even or odd) of the number must always be the same
	 * 
	 * @param permutation1
	 * @param permutation2
	 * @return
	 */
	public static int getNumOfPairSwitches(int permutation1[], int permutation2[])
	{
		int n = 0;
		int perm[] = permutation1.clone();
		
		
		//TODO
		return n;
	}
	
	public static void switchPos(int pos1, int pos2, int array[])
	{
		int temp = array[pos1];
		array[pos1] = array[pos2];
		array[pos2] = temp;
	}
	
	
}
