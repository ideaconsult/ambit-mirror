package ambit2.groupcontribution.utils.math;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MathUtils 
{
	private Random random;
	
	public MathUtils()
	{
		initialize();
	}
		
	public void initialize()
	{
		random = new Random();
		random.setSeed(System.currentTimeMillis());
	}
		
	public void fillArrayWithRandomIntegers(int[] objs, int maxValue)
	{
		int num = objs.length;
		if (num > maxValue)
			return;
		
		List<Integer> v = new ArrayList<Integer>();
		for (int i = 0; i < maxValue; ++i)
			v.add(new Integer(i));
		
		for (int i = 0; i < num; ++i)
		{
			int elNum = random.nextInt(v.size());
			objs[i] = v.get(elNum);
			v.remove(elNum);
		}
	}
	
	public int[] getRandomPermutation(int n)
	{
		int tmp;
		int p[] = new int[n];
		for (int i=0; i < n; i++)
			p[i] = i;
		
		for (int i=0; i<n-1; i++)
		{
			int k = (i+1) + random.nextInt(n-i-1);
			//p[i]  <-->  p[k]
			tmp = p[k];
			p[k] = p[i];
			p[i] = tmp;
		}		
		return (p);
	}
	
	public static int firstIndexOf(int value, int a[])
	{	
		if (a == null)
			return(-1);
		for (int i = 0; i < a.length; i++)
			if (a[i]== value)
				return(i);
		
		return(-1);
	}
	
	public static int firstIndexOf(double value, double a[])
	{	
		if (a == null)
			return(-1);
		
		for (int i = 0; i < a.length; i++)
			if (a[i]== value)
				return(i);
		
		return(-1);
	}
	
	public static void fillArrayWith(int value, int a[])
	{	
		for (int i = 0; i < a.length; i++)
			a[i]= value;
	}
	
	public static void fillArrayWith(double value, double a[])
	{	
		for (int i = 0; i < a.length; i++)
			a[i]= value;
	}
	
}
