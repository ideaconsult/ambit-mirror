package ambit2.smarts;

import java.util.BitSet;

import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

public class Screening 
{
	ScreeningData querySD = new ScreeningData();
	IAtomContainer queryAC;
	Fingerprinter fp = new Fingerprinter();
	SmartsToChemObject convertor = new SmartsToChemObject();
	
	public Screening()
	{	
	}
	
	
	public void setQuery(QueryAtomContainer query)
	{	
		try
		{
			queryAC = convertor.extractAtomContainer(query);
			querySD.fingerprint = fp.getFingerprint(queryAC);
		}
		catch (Exception e)
		{	
		}
	}
	
	public boolean screenTest(ScreeningData targetSD)
	{		
		return false;
	}
	
	public ScreeningData getScreeningDataFor(IAtomContainer ac)
	{
		ScreeningData sd = new ScreeningData();
		try
		{
			sd.fingerprint = fp.getFingerprint(queryAC);
		}
		catch (Exception e)
		{	
		}
		return sd;
	}
}
