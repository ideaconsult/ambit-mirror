package ambit2.smarts;

import org.openscience.cdk.interfaces.IBond;

public class ClosureBond 
{
	//This class is used for IAtomContainers. (RingClosure is used for QueryAtomContaner)
	public int at1;
	public int at2;
	public IBond bt;
	public int index;
	
	public String getIndexAt1()
	{
		if (index > 9)
			return("%" + index);
		else
			return("" + index);
	}
	
	
	public String getIndexAt2(boolean aromaticity)
	{		
		String boString = SmartsHelper.smilesBondToString(bt, aromaticity);
		
		if (index > 9)
			return(boString+"%" + index);
		else
			return(boString + index);
	}
	
	boolean isEqualTo(ClosureBond cb)
	{
		if (cb.at1 == at1)
		{
			if (cb.at2 == at2)
				return(true);
		}
		else
			if (cb.at1 == at2)
			{
				if (cb.at2 == at1)
					return(true);
			}
			
		return(false);
	}
}
