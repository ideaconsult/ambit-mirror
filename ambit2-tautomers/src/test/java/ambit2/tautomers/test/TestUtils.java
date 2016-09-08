package ambit2.tautomers.test;

import ambit2.tautomers.tools.TautomerAnalysis;

public class TestUtils {

	public static void main(String[] args) 
	{
		testRanking(new double[] {3,6,1,9.5,3});

	}
	
	
	public static void testRanking(double values[])
	{
		double ranks[] = TautomerAnalysis.getRanks(values);
		for (int i = 0; i < ranks.length; i++)
			System.out.println("  " + values[i] + "   rank = " + ranks[i]);
	}

}
