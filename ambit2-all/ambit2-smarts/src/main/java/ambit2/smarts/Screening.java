package ambit2.smarts;

import java.util.BitSet;
import java.util.Vector;

import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

public class Screening 
{
	IsomorphismTester isoTester = new IsomorphismTester(); 
	ScreeningData querySD = new ScreeningData();
	IAtomContainer extractedQueryAC;	
	Fingerprinter fp = new Fingerprinter();
	SmartsToChemObject convertor = new SmartsToChemObject();
	SmartsScreeningKeys smartsScrKeys = new SmartsScreeningKeys(); 
	Vector<Vector<QuerySequenceElement>> sequences = new Vector<Vector<QuerySequenceElement>>(); 
	SmartsParser parser = new SmartsParser(); 
	boolean FlagUseStrKeys = true;
		
	
	public Screening(boolean useStrKeys)	
	{
		FlagUseStrKeys = useStrKeys;
		if (FlagUseStrKeys)
			prepareKeySequences();
	}
	
	
	public void setQuery(QueryAtomContainer query)
	{	
		try
		{
			extractedQueryAC = convertor.extractAtomContainer(query);			
			querySD = getScreeningDataForTarget(extractedQueryAC);
		}
		catch (Exception e)
		{	
		}
	}
	
	public boolean checkTarget(ScreeningData targetSD)
	{		
		if (!bitSetCheck(querySD.fingerprint, targetSD.fingerprint))
			return(false);
		
		if (FlagUseStrKeys)
		{
			if (!bitSetCheck(querySD.structureKeys, targetSD.structureKeys))
				return(false);
		}		
		return true;
	}
	
	
	boolean bitSetCheck(BitSet query, BitSet target)
	{
		for (int i = 0; i < query.size(); i++)
		{	
			if (query.get(i))
			{
				if (!target.get(i))
					return (false);
			}
		}	
		return (true);
	}
		
	public ScreeningData getScreeningDataForTarget(IAtomContainer ac)
	{
		ScreeningData sd = new ScreeningData();
		try
		{
			sd.fingerprint = fp.getFingerprint(ac);
			if (FlagUseStrKeys)
				sd.structureKeys = getStructureKeyBits(ac);
		}
		catch (Exception e)
		{	
		}
		return sd;
	}
	
	public BitSet getStructureKeyBits(IAtomContainer ac)
	{
		BitSet keys = new BitSet(smartsScrKeys.nKeys);
		boolean res;
		for (int i = 0; i < smartsScrKeys.nKeys; i++) 
		{
			isoTester.setSequence(sequences.get(i));
			res = isoTester.hasIsomorphism(ac);
			keys.set(i, res);
		}
		
		return(keys);
	}
	
	void prepareKeySequences()
	{
		QueryAtomContainer query;
		
		sequences.clear();
		Vector<String> smartsKeys = smartsScrKeys.getKeys();
		
		for (int i = 0; i < smartsScrKeys.nKeys; i++)
		{
			query = parser.parse(smartsKeys.get(i));			
			//parser.setNeededDataFlags();       --> This should not be needed for the key smarts queries
			isoTester.setQuery(query);
			Vector<QuerySequenceElement> sequence = isoTester.transferSequenceToOwner();
			sequences.add(sequence);
		}
	}
}
