package ambit2.tautomers.test;

import ambit2.tautomers.tools.TautomerAnalysis;

public class TestUtils {

	public static void main(String[] args) 
	{
		testRanking(new double[] {2,3,3,0,45,1,3});

	}
	
	
	public static void testRanking(double values[])
	{
		double ranks[] = TautomerAnalysis.getRanks(values, true);
		System.out.println("Original values + ranks:");
		for (int i = 0; i < ranks.length; i++)			
			System.out.println("  " + values[i] + "   rank = " + ranks[i]);
		
		int n = values.length;
		for (int i = 1; i < n; i++)
			for (int k = 0; k < n-i; k++)
			{
				if (ranks[k] >  ranks[k+1])
				{
					//swap ranks
					double tmp = ranks[k+1];
					ranks[k+1] = ranks[k];
					ranks[k] = tmp;
					//swap values
					tmp = values[k+1];
					values[k+1] = values[k];
					values[k] = tmp;
				}
			}
			
		
		System.out.println("Values sorted by ranking:");
		for (int i = 0; i < ranks.length; i++)			
			System.out.println("  " + values[i] + "   rank = " + ranks[i]);
		
	}

}
