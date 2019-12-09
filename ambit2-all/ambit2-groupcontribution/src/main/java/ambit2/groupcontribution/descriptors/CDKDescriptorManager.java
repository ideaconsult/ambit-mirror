package ambit2.groupcontribution.descriptors;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.APolDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.AromaticAtomsCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.BPolDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.WienerNumbersDescriptor;
import org.openscience.cdk.qsar.result.BooleanResult;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.qsar.result.IntegerResult;

import ambit2.groupcontribution.transformations.IValueTransformation;

public class CDKDescriptorManager 
{
	public List<IMolecularDescriptor> descriptorInstances = new ArrayList<IMolecularDescriptor>();
	public List<CDKDescriptorInfo> descriptors = new ArrayList<CDKDescriptorInfo>();
	public List<String> errors = new ArrayList<String>();
	
	
	public int getDescrInstanceIndex(String className)
	{	
		for (int i = 0; i < descriptorInstances.size(); ++i)
			if (descriptorInstances.get(i).getClass().getName().equals(className))
				return(i);
		return(-1); 
	}
	
	
	public CDKDescriptorInfo registerDecriptor(String fullString, String name, Class descrClass, 
			int resPos, int hAtomsFlag, IValueTransformation valueTranform)
	{	
		int index = getDescrInstanceIndex(descrClass.getName());			
		if (index == -1)
		{	
			index = descriptorInstances.size();
			try{
				descriptorInstances.add((IMolecularDescriptor)descrClass.newInstance());
			}
			catch(Exception e){
				errors.add("Errors on registering " + descrClass.getName() 
				+ " : " + e.getMessage());				
				return null;
			}
		}
		
		CDKDescriptorInfo di = new CDKDescriptorInfo(); 
		di.fullString = fullString;
		di.name = name;
		di.resultPos = resPos;
		di.valueTranform = valueTranform;
		descriptors.add(di);
		return di;
	}
	
	public void parseDecriptor(String descrString)
	{	
		String dname = descrString;
		String fullString = descrString;
		IValueTransformation vt = null;
		CDKDescriptorInfo di = null;
		
		if (dname.equals("W") || dname.equals("WI") || dname.equals("WienerIndex"))			
			di = registerDecriptor(fullString, dname, WienerNumbersDescriptor.class, 0, 0, vt);
		else if (dname.equals("WP"))
			di = registerDecriptor(fullString, dname, WienerNumbersDescriptor.class, 1, 0, vt);		
		else if (dname.equals("APol"))
			di = registerDecriptor(fullString, dname, APolDescriptor.class, 0, 1, vt);
		else if (dname.equals("BPol"))
			di = registerDecriptor(fullString, dname, BPolDescriptor.class, 0, 1, vt);
		else if (dname.equals("NAA") || dname.equals("AromaticAtomsCount") || dname.equals("AAC"))
			di = registerDecriptor(fullString, dname, AromaticAtomsCountDescriptor.class, 0, 0, vt);
		
	}	
	
	public static double getDescritptor(IAtomContainer mol, IMolecularDescriptor descr, int n)
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
	
	public static double getDescriptorFromDescrValue(DescriptorValue v, int n)
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
