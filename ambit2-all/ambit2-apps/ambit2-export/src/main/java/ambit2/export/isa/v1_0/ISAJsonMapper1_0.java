package ambit2.export.isa.v1_0;

import ambit2.export.isa.base.IISADataMapper;
import ambit2.export.isa.base.ISALocation;
import ambit2.export.isa.v1_0.objects.Investigation;

public class ISAJsonMapper1_0 implements IISADataMapper
{

	protected Investigation investigation = null;
	
	@Override
	public void putObject(Object o, ISALocation location) throws Exception 
	{
		if (o == null)
			return;
		
		if (o instanceof Integer)
		{	
			putInteger((Integer) o, location);
			return;
		}
		
		if (o instanceof Double)
		{	
			putDouble((Double) o, location);
			return;
		}
		
		if (o instanceof String)
		{	
			putString((String) o, location);
			return;
		}
		
		//By default object is treated as string
		putString(o.toString(), location);
	}
	
	@Override
	public void setTargetDataObject(Object target) throws Exception 
	{
		if (target instanceof Investigation)
			investigation = (Investigation) target;
		else
			throw new Exception("Target object is not instance of Investigation class!");
	}
	
	@Override
	public void putInteger(Integer k, ISALocation location) throws Exception 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void putDouble(Double d, ISALocation location) throws Exception 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void putString(String s, ISALocation location) throws Exception 
	{
		// TODO Auto-generated method stub
		
	}
}
