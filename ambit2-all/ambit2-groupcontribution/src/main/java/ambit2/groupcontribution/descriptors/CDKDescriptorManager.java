package ambit2.groupcontribution.descriptors;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.APolDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.AromaticAtomsCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.AromaticBondsCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.BPolDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.ChiChainDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.ChiClusterDescriptor;
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
		di.descrInstanceIndex = index;
		di.name = name;
		di.resultPos = resPos;
		di.hAtomsFlag = hAtomsFlag; 
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
		else if (dname.equals("NAB") || dname.equals("AromaticBondsCount") || dname.equals("ABC"))
			di = registerDecriptor(fullString, dname, AromaticBondsCountDescriptor.class, 0, 0, vt);
		else if (dname.equals("BCUTw-Lo"))
			di = registerDecriptor(fullString, dname, BCUTDescriptor.class, 0, 0, vt);
		else if (dname.equals("BCUTw-Hi"))
			di = registerDecriptor(fullString, dname, BCUTDescriptor.class, 1, 0, vt);
		else if (dname.equals("BCUTc-Lo"))
			di = registerDecriptor(fullString, dname, BCUTDescriptor.class, 2, 0, vt);
		else if (dname.equals("BCUTc-Hi"))
			di = registerDecriptor(fullString, dname, BCUTDescriptor.class, 3, 0, vt);
		else if (dname.equals("BCUTp-Lo"))
			di = registerDecriptor(fullString, dname, BCUTDescriptor.class, 4, 0, vt);
		else if (dname.equals("BCUTp-Hi"))
			di = registerDecriptor(fullString, dname, BCUTDescriptor.class, 5, 0, vt);
		else if (dname.equals("SCH3"))
			di = registerDecriptor(fullString, dname, ChiChainDescriptor.class, 0, 0, vt);
		else if (dname.equals("SCH4"))
			di = registerDecriptor(fullString, dname, ChiChainDescriptor.class, 1, 0, vt);
		else if (dname.equals("SCH5"))
			di = registerDecriptor(fullString, dname, ChiChainDescriptor.class, 2, 0, vt);
		else if (dname.equals("SCH6"))
			di = registerDecriptor(fullString, dname, ChiChainDescriptor.class, 3, 0, vt);
		else if (dname.equals("VCH3"))
			di = registerDecriptor(fullString, dname, ChiChainDescriptor.class, 4, 0, vt);
		else if (dname.equals("VCH4"))
			di = registerDecriptor(fullString, dname, ChiChainDescriptor.class, 5, 0, vt);
		else if (dname.equals("VCH5"))
			di = registerDecriptor(fullString, dname, ChiChainDescriptor.class, 6, 0, vt);
		else if (dname.equals("VCH6"))
			di = registerDecriptor(fullString, dname, ChiChainDescriptor.class, 7, 0, vt);
		else if (dname.equals("SC3"))
			di = registerDecriptor(fullString, dname, ChiClusterDescriptor.class, 0, 0, vt);
		else if (dname.equals("SC4"))
			di = registerDecriptor(fullString, dname, ChiClusterDescriptor.class, 1, 0, vt);
		else if (dname.equals("SC5"))
			di = registerDecriptor(fullString, dname, ChiClusterDescriptor.class, 2, 0, vt);
		else if (dname.equals("SC6"))
			di = registerDecriptor(fullString, dname, ChiClusterDescriptor.class, 3, 0, vt);
		else if (dname.equals("VC3"))
			di = registerDecriptor(fullString, dname, ChiClusterDescriptor.class, 4, 0, vt);
		else if (dname.equals("VC4"))
			di = registerDecriptor(fullString, dname, ChiClusterDescriptor.class, 5, 0, vt);
		else if (dname.equals("VC5"))
			di = registerDecriptor(fullString, dname, ChiClusterDescriptor.class, 6, 0, vt);
		else if (dname.equals("VC6"))
			di = registerDecriptor(fullString, dname, ChiClusterDescriptor.class, 7, 0, vt);
		
		
		else
		{
			errors.add("Incorrect/unknown descriptor: " + fullString);
		}
	}
	
	public List<DescriptorValue> calcDecriptorValuesForMolecule(IAtomContainer mol, IAtomContainer molH)
	{
		List<DescriptorValue> dValues = new ArrayList<DescriptorValue>();
		
		for (int i = 0; i < descriptors.size(); ++i)
		{
			IAtomContainer m;
			if (descriptors.get(i).hAtomsFlag == 0)
				m = mol;
			else
				m = molH;
			
			try
			{
				int instanceIndex = descriptors.get(i).descrInstanceIndex;
				DescriptorValue v = descriptorInstances.get(instanceIndex).calculate(m);
				dValues.add(v);
			}	
			catch (Exception e){
			}
		}
		
		return dValues;
	}
	
	public double getDecriptor(int descrIndex, List<DescriptorValue> dValues)
	{
		int resPos = descriptors.get(descrIndex).resultPos;
		double d = getDescriptorFromDescrValue(dValues.get(descrIndex), resPos);
		return(d);
	}
	
	
	public static double calcDescritptor(IAtomContainer mol, IMolecularDescriptor descr, int n)
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
	
	public static double getDescriptorFromDescrValue(DescriptorValue v, int resPos)
	{	
		try
		{				
			
			if (v.getValue() instanceof DoubleArrayResult)
			{
				DoubleArrayResult d = (DoubleArrayResult)v.getValue();
				return(d.get(resPos));
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
				return(i.get(resPos));
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
