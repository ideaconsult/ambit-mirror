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
import org.openscience.cdk.qsar.descriptors.molecular.ChiPathDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.EccentricConnectivityIndexDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.FragmentComplexityDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.HBondAcceptorCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.HBondDonorCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.KappaShapeIndicesDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.LargestChainDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.LargestPiSystemDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.LongestAliphaticChainDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.MDEDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.PetitjeanNumberDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.RotatableBondsCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.RuleOfFiveDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.TPSADescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.VAdjMaDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.WeightDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.WienerNumbersDescriptor;
import org.openscience.cdk.qsar.result.BooleanResult;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.qsar.result.IntegerResult;

import ambit2.groupcontribution.transformations.IValueTransformation;
import ambit2.groupcontribution.transformations.TransformationComposition;
import ambit2.groupcontribution.transformations.TransformationUtils;

public class CDKDescriptorManager 
{
	public List<IMolecularDescriptor> descriptorInstances = new ArrayList<IMolecularDescriptor>();
	public List<CDKDescriptorInfo> descriptors = new ArrayList<CDKDescriptorInfo>();
	public List<String> errors = new ArrayList<String>();
	public TransformationUtils transfUtils = new TransformationUtils(); 
	
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
	
	public CDKDescriptorInfo parseDecriptor(String descrString)
	{	
		String dname = null;
		String fullString = descrString;
		IValueTransformation vt = null;
		
		int sepIndex = descrString.indexOf(TransformationComposition.composeSeparator);
		if (sepIndex == -1)
			dname = descrString;
		else
		{
			dname = descrString.substring(0, sepIndex).trim();
			String transfStr = descrString.substring(sepIndex + 
							TransformationComposition.composeSeparator.length()).trim();
			
			vt = transfUtils.parseTransformation(transfStr);
			if (vt == null)
				errors.addAll(transfUtils.errors);
		}
		
		
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
		else if (dname.equals("SP0"))
			di = registerDecriptor(fullString, dname, ChiPathDescriptor.class, 0, 0, vt);
		else if (dname.equals("SP1"))
			di = registerDecriptor(fullString, dname, ChiPathDescriptor.class, 1, 0, vt);
		else if (dname.equals("SP2"))
			di = registerDecriptor(fullString, dname, ChiPathDescriptor.class, 2, 0, vt);
		else if (dname.equals("SP3"))
			di = registerDecriptor(fullString, dname, ChiPathDescriptor.class, 3, 0, vt);
		else if (dname.equals("SP4"))
			di = registerDecriptor(fullString, dname, ChiPathDescriptor.class, 4, 0, vt);
		else if (dname.equals("SP5"))
			di = registerDecriptor(fullString, dname, ChiPathDescriptor.class, 5, 0, vt);
		else if (dname.equals("SP6"))
			di = registerDecriptor(fullString, dname, ChiPathDescriptor.class, 6, 0, vt);
		else if (dname.equals("SP7"))
			di = registerDecriptor(fullString, dname, ChiPathDescriptor.class, 7, 0, vt);
		else if (dname.equals("VP0"))
			di = registerDecriptor(fullString, dname, ChiPathDescriptor.class, 8, 0, vt);
		else if (dname.equals("VP1"))
			di = registerDecriptor(fullString, dname, ChiPathDescriptor.class, 9, 0, vt);
		else if (dname.equals("VP2"))
			di = registerDecriptor(fullString, dname, ChiPathDescriptor.class, 10, 0, vt);
		else if (dname.equals("VP3"))
			di = registerDecriptor(fullString, dname, ChiPathDescriptor.class, 11, 0, vt);
		else if (dname.equals("VP4"))
			di = registerDecriptor(fullString, dname, ChiPathDescriptor.class, 12, 0, vt);
		else if (dname.equals("VP5"))
			di = registerDecriptor(fullString, dname, ChiPathDescriptor.class, 13, 0, vt);
		else if (dname.equals("VP6"))
			di = registerDecriptor(fullString, dname, ChiPathDescriptor.class, 14, 0, vt);
		else if (dname.equals("VP7"))
			di = registerDecriptor(fullString, dname, ChiPathDescriptor.class, 15, 0, vt);
		else if (dname.equals("ECCEN"))
			di = registerDecriptor(fullString, dname, EccentricConnectivityIndexDescriptor.class, 0, 0, vt);
		else if (dname.equals("FragCompl"))
			di = registerDecriptor(fullString, dname, FragmentComplexityDescriptor.class, 0, 0, vt);
		else if (dname.equals("NHBACC"))
			di = registerDecriptor(fullString, dname, HBondAcceptorCountDescriptor.class, 0, 1, vt);
		else if (dname.equals("NHBDON"))
			di = registerDecriptor(fullString, dname, HBondDonorCountDescriptor.class, 0, 1, vt);
		else if (dname.equals("Kappa1"))
			di = registerDecriptor(fullString, dname, KappaShapeIndicesDescriptor.class, 0, 0, vt);
		else if (dname.equals("Kappa2"))
			di = registerDecriptor(fullString, dname, KappaShapeIndicesDescriptor.class, 1, 0, vt);
		else if (dname.equals("Kappa3"))
			di = registerDecriptor(fullString, dname, KappaShapeIndicesDescriptor.class, 2, 0, vt);
		else if (dname.equals("LargestChain"))
			di = registerDecriptor(fullString, dname, LargestChainDescriptor.class, 0, 0, vt);
		else if (dname.equals("LargestPiSystem"))
			di = registerDecriptor(fullString, dname, LargestPiSystemDescriptor.class, 0, 0, vt);
		else if (dname.equals("LongestAliphaticChain"))
			di = registerDecriptor(fullString, dname, LongestAliphaticChainDescriptor.class, 0, 0, vt);
		else if (dname.equals("MDEC11"))
			di = registerDecriptor(fullString, dname, MDEDescriptor.class, 0, 1, vt);
		else if (dname.equals("MDEC12"))
			di = registerDecriptor(fullString, dname, MDEDescriptor.class, 1, 1, vt);
		else if (dname.equals("MDEC13"))
			di = registerDecriptor(fullString, dname, MDEDescriptor.class, 2, 1, vt);
		else if (dname.equals("MDEC14"))
			di = registerDecriptor(fullString, dname, MDEDescriptor.class, 3, 1, vt);
		else if (dname.equals("MDEC22"))
			di = registerDecriptor(fullString, dname, MDEDescriptor.class, 4, 1, vt);
		else if (dname.equals("MDEC23"))
			di = registerDecriptor(fullString, dname, MDEDescriptor.class, 5, 1, vt);
		else if (dname.equals("MDEC24"))
			di = registerDecriptor(fullString, dname, MDEDescriptor.class, 6, 1, vt);
		else if (dname.equals("MDEC33"))
			di = registerDecriptor(fullString, dname, MDEDescriptor.class, 7, 1, vt);
		else if (dname.equals("MDEC34"))
			di = registerDecriptor(fullString, dname, MDEDescriptor.class, 8, 1, vt);
		else if (dname.equals("MDEC44"))
			di = registerDecriptor(fullString, dname, MDEDescriptor.class, 9, 1, vt);
		else if (dname.equals("MDEO11"))
			di = registerDecriptor(fullString, dname, MDEDescriptor.class, 10, 1, vt);
		else if (dname.equals("MDEO12"))
			di = registerDecriptor(fullString, dname, MDEDescriptor.class, 11, 1, vt);
		else if (dname.equals("MDEO22"))
			di = registerDecriptor(fullString, dname, MDEDescriptor.class, 12, 1, vt);
		else if (dname.equals("MDEN11"))
			di = registerDecriptor(fullString, dname, MDEDescriptor.class, 13, 1, vt);
		else if (dname.equals("MDEN12"))
			di = registerDecriptor(fullString, dname, MDEDescriptor.class, 14, 1, vt);
		else if (dname.equals("MDEN13"))
			di = registerDecriptor(fullString, dname, MDEDescriptor.class, 15, 1, vt);
		else if (dname.equals("MDEN22"))
			di = registerDecriptor(fullString, dname, MDEDescriptor.class, 16, 1, vt);
		else if (dname.equals("MDEN23"))
			di = registerDecriptor(fullString, dname, MDEDescriptor.class, 17, 0, vt);
		else if (dname.equals("MDEN33"))
			di = registerDecriptor(fullString, dname, MDEDescriptor.class, 18, 0, vt);
		else if (dname.equals("PetitjeanNumber"))
			di = registerDecriptor(fullString, dname, PetitjeanNumberDescriptor.class, 0, 1, vt);
		//else if (dname.equals("PetitjeanShapeIndex-topo"))
		//	registerDecriptor(PetitjeanShapeIndexDescriptor.class, 0, 1);
		//else if (dname.equals("PetitjeanShapeIndex-geom"))
		//	registerDecriptor(PetitjeanShapeIndexDescriptor.class, 1, 1);
		else if (dname.equals("NROTB"))
			di = registerDecriptor(fullString, dname, RotatableBondsCountDescriptor.class, 0, 1, vt);
		else if (dname.equals("RuleOf5")||dname.equals("LipinskiFailures"))
			di = registerDecriptor(fullString, dname, RuleOfFiveDescriptor.class, 0, 1, vt);
		else if (dname.equals("TPSA"))
			di = registerDecriptor(fullString, dname, TPSADescriptor.class, 0, 1, vt);
		else if (dname.equals("VAdjMa"))
			di = registerDecriptor(fullString, dname, VAdjMaDescriptor.class, 0, 0, vt);
		//else if (dname.equals("VCC0") || dname.equals("ValenceCarbonConnectivity0"))
		//	registerDecriptor(ValenceCarbonConnectivityOrderZeroDescriptor.class, 0, 0);
		//else if (dname.equals("VCC1") || dname.equals("ValenceCarbonConnectivity1"))
		//	registerDecriptor(ValenceCarbonConnectivityOrderOneDescriptor.class, 0, 0);
		else if (dname.equals("MW") || dname.equals("Weight"))
			di = registerDecriptor(fullString, dname, WeightDescriptor.class, 0, 1, vt);		
		
		else
		{
			errors.add("Incorrect/unknown descriptor: " + fullString);
			return null;
		}
		
		return di;
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
