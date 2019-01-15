package ambit2.structure2name;

import ambit2.structure2name.rules.IUPACRuleDataBase;

public class TestIUPACUtils 
{
	public static void main(String[] args) throws Exception 
	{
		testIUPACRuleDB();
	}
	
	public static void testIUPACRuleDB() throws Exception
	{
		IUPACRuleDataBase rdb = IUPACRuleDataBase.getDefaultRuleDataBase();
	}
}
