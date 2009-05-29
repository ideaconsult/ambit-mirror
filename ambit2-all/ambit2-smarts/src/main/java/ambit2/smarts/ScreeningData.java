package ambit2.smarts;

import java.util.BitSet;

/**
 * 
 * @author nina
 *
 */
public class ScreeningData 
{
	public BitSet structureKeys = null;
	public BitSet fingerprint = null;
	
	boolean checkStructureKeys(ScreeningData sd)
	{	
		for(int i=structureKeys.nextSetBit(0); i>=0; i=structureKeys.nextSetBit(i+1))
		{
			if (!sd.structureKeys.get(i));
				return(false);
		}
		return(true);
	}
	
	boolean checkFingerprint(ScreeningData sd)
	{	
		for(int i=fingerprint.nextSetBit(0); i>=0; i=fingerprint.nextSetBit(i+1))
		{
			if (!sd.fingerprint.get(i));
				return(false);
		}
		return(true);
	}
	
}
