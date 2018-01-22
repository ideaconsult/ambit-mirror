package ambit2.rules.test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.junit.Test;

import ambit2.rules.functions.IFunction;
import ambit2.rules.functions.LinearFunction;
import ambit2.rules.functions.NestedFunctions;
import ambit2.rules.functions.ReciprocalFunction;
import junit.framework.Assert;

public class TestFunctions 
{
	static Logger logger = Logger.getLogger(TestFunctions.class.getName());
	static double eps = 1.0E-20;

	@Test
	public void testLinearFunction()
	{
		LinearFunction fun = new LinearFunction(new double [] {3,5});
		double x_array[] = {1,2,5,100,200,500, 33, -1000, -19}; 
		for (double x: x_array)
		{	
			Assert.assertEquals("3 * x + 5  for x = " + x, true, 
					Math.abs(3.0*x +5.0 - fun.getFunctionValue(x)) < eps);
		}	
	}
	
	@Test
	public void testReciprocalFunction()
	{
		ReciprocalFunction fun = new ReciprocalFunction();
		double x_array[] = {1,2,5,100,200,500,3,7,11,-103,-17}; 
		for (double x: x_array)
		{	
			//System.out.println(fun.getFunctionValue(x) + "    " + (Math.abs(1.0/x + - fun.getFunctionValue(x))));
			Assert.assertEquals("1/x for x = " + x, true,
					Math.abs(1.0/x + - fun.getFunctionValue(x)) < eps);
		}	
		
	}
	
	@Test
	public void testLinearOfReciprocalFunction()
	{
		List<IFunction> functions = new ArrayList<IFunction>();
		functions.add(new ReciprocalFunction());
		functions.add(new LinearFunction(new double [] {2,7}));
		NestedFunctions fun = new NestedFunctions();
		fun.setFunctions(functions);
		
		double x_array[] = {1,2,5,100,200,500,3,7,11,-24,-101}; 
		for (double x: x_array)
		{	
			//System.out.println(fun.getFunctionValue(x) + "    " + (Math.abs(2.0*(1/x)+7.0 - fun.getFunctionValue(x))));
			Assert.assertEquals("2*(1/x)+7 for x = " + x, true,
					Math.abs(2.0*(1/x)+7.0 - fun.getFunctionValue(x)) < eps);
		}	
		
	}
	
}
