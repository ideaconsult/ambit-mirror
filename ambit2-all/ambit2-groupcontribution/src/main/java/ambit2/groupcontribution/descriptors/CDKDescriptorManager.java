package ambit2.groupcontribution.descriptors;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.BooleanResult;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.qsar.result.IntegerResult;

public class CDKDescriptorManager 
{
	
	public double getDescritptor(IAtomContainer mol, IMolecularDescriptor descr, int n)
	{
		try
		{
			DescriptorValue v = descr.calculate(mol);
			return(getDescriptorFromDescrValue(v,n));
		}
		catch(Exception e)
		{
			System.out.println(e.toString());			
		}
		return(0);
	}
	
	public double getDescriptorFromDescrValue(DescriptorValue v, int n)
	{	
		try
		{				
			
			if (v.getValue() instanceof DoubleArrayResult)
			{
				DoubleArrayResult d = (DoubleArrayResult)v.getValue();
				return(d.get(n));
			}
			else
			if (v.getValue() instanceof DoubleResult)
			{
				DoubleResult d = (DoubleResult)v.getValue();
				return(d.doubleValue());
			}
			else
			if (v.getValue() instanceof IntegerArrayResult)
			{
				IntegerArrayResult i = (IntegerArrayResult)v.getValue();
				return(i.get(n));
			}
			else
			if (v.getValue() instanceof IntegerResult)
			{
				IntegerResult i = (IntegerResult)v.getValue();
				return(i.intValue());
			}
			else			
			if (v.getValue() instanceof BooleanResult)
			{
				BooleanResult b = (BooleanResult)v.getValue();
				if (b.booleanValue())
					return(1);
				else
					return(0);
			}	
		}
		catch(Exception e)
		{
			
		}
		
		return(0);
	}
	
}
