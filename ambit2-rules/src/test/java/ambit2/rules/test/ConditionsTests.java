package ambit2.rules.test;

import org.openscience.cdk.tools.LoggingTool;

import ambit2.rules.conditions.DescriptorValueCondition;
import ambit2.rules.conditions.ValueCondition;
import ambit2.rules.conditions.parser.ConditionParsingUtils;
import ambit2.rules.conditions.value.Value;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ConditionsTests extends TestCase
{
	public LoggingTool logger;
	
	public ConditionsTests() 
	{   
		logger = new LoggingTool(this);
	}
	
	public static Test suite() {
		return new TestSuite(ConditionsTests.class);
	}
	
	void checkValueCondition(double target, String relString, double dValue, boolean expectedResult)
	{
		Value value = new Value (dValue, relString);
		ValueCondition condition = new ValueCondition(value);
		boolean res = condition.isTrue(target);
		assertEquals("condition: " + target + " " + relString + " " + dValue, expectedResult, res);
	}
	
	void checkDescriptorValueCondition(String descrCondString, DescriptorSolver0 solver0, boolean expectedResult) throws Exception
	{
		DescriptorValueCondition dvc = ConditionParsingUtils.getDescriptorValueConditionFromToken(descrCondString);
		dvc.setDescriptorSolver(solver0);
		System.out.println(dvc.toString());
		boolean res = dvc.isTrue(new String("S000")); //The target object is not taken into account by solver0 
		assertEquals("condition: " + descrCondString, expectedResult, res);
	}
	
	
	public void testValueConditions()
	{
		checkValueCondition(3, ">=", 5, false);
		checkValueCondition(3, ">=", -5, true);
		checkValueCondition(3, ">", 1, true);
		checkValueCondition(3, "<", 1, false);
		checkValueCondition(3, "<=", 15, true);
		checkValueCondition(15, "<=", 15, true);
		checkValueCondition(15, ">=", 15, true);
		checkValueCondition(3, "=", 3, true);
		checkValueCondition(3, "!=", 3, false);
	}
	
	
	public void testDescriptorValueConditions() throws Exception
	{
		DescriptorSolver0 solver0 = new DescriptorSolver0();
		solver0.addDescriptor("d1", 30.0);
		solver0.addDescriptor("my_descr", 50.0);
		solver0.addDescriptor("tt2", 150.0);
		
		checkDescriptorValueCondition("d1 > 10.0", solver0, true);
		checkDescriptorValueCondition("d1 < 10.0", solver0, false);
		checkDescriptorValueCondition("my_descr <= 35.0", solver0, false);
		checkDescriptorValueCondition("my_descr <= 50.0", solver0, true);
		checkDescriptorValueCondition("my_descr >= 35.0", solver0, true);		
		checkDescriptorValueCondition("my_descr >= 50.0", solver0, true);
		checkDescriptorValueCondition("my_descr == 50.0", solver0, true);
		checkDescriptorValueCondition("my_descr != 15.0", solver0, true);
		checkDescriptorValueCondition("!!tt2 != 123", solver0, true);
		checkDescriptorValueCondition("!tt2 < 123", solver0, true);		
		checkDescriptorValueCondition("!tt2 > 140", solver0, false);
	}
	
}
