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
		for (int i = 0; i < rdb.carbonData.length; i++)
		{
			System.out.println(rdb.carbonData[i].toString());
		}
	}
}
