package ambit2.smarts.processors;

import java.util.BitSet;
import java.util.Vector;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.QuerySequenceElement;
import ambit2.smarts.SmartsParser;
import ambit2.smarts.SmartsScreeningKeys;

/**
 * Generates screening fingerprints / structural keys for smarts queries 
 * @author nina
 *
 */
public class StructureKeysBitSetGenerator extends DefaultAmbitProcessor<IAtomContainer, BitSet> {
	protected IsomorphismTester isoTester = new IsomorphismTester(); 
	protected static Vector<QueryAtomContainer> smartsQueries = null;	
	protected static Vector<Vector<QuerySequenceElement>> sequences = null; 
	protected static Vector<String> smartsKeys; //not really needed, except for printing structure keys
	protected static int nKeys;
	
	protected FingerprintGenerator fp = new FingerprintGenerator();
	/**
	 * 
	 */
	private static final long serialVersionUID = 696373923085520847L;
	
	public StructureKeysBitSetGenerator() {
		if ((smartsQueries==null) || (sequences==null)) {
			smartsQueries = new Vector<QueryAtomContainer>();
			sequences = new Vector<Vector<QuerySequenceElement>>();
			SmartsScreeningKeys smartsScrKeys = new SmartsScreeningKeys(); 
			nKeys = smartsScrKeys.nKeys;
			prepareKeySequences(smartsScrKeys.getKeys(),smartsScrKeys.nKeys);			
		}

	}	
	public StructureKeysBitSetGenerator(Vector<String> externalSmartsKeys)	
	{
		setSmartsKeys(externalSmartsKeys);

	}	
	public void setSmartsKeys(Vector<String> smartsKeys) {
		prepareKeySequences(smartsKeys,smartsKeys.size());		
	}
	protected void prepareKeySequences(Vector<String> keys, int nKeys)
	{
		smartsKeys = keys; 
		QueryAtomContainer query;
		sequences.clear();
		smartsQueries.clear();
		SmartsParser parser = new SmartsParser();
		for (int i = 0; i < nKeys; i++)
		{
			query = parser.parse(smartsKeys.get(i));
			
			//parser.setNeededDataFlags();       --> This should not be needed for the key smarts queries
			isoTester.setQuery(query);
			Vector<QuerySequenceElement> sequence = isoTester.transferSequenceToOwner();
			sequences.add(sequence);
			smartsQueries.add(query);
		}
	}	
	
	
	public BitSet process(IAtomContainer target) throws AmbitException {
		return getStructureKeyBits(target);
	}
	
	protected BitSet getStructureKeyBits(IAtomContainer ac)
	{
		BitSet keys = new BitSet(nKeys);
		boolean res;
		for (int i = 0; i < nKeys; i++) 
		{
			isoTester.setSequence(smartsQueries.get(i), sequences.get(i));
			res = isoTester.hasIsomorphism(ac);
			keys.set(i, res);
			//System.out.println(smartsKeys.get(i) + "  " + res);
		}
		return(keys);
	}
	
	public static String getKey(int index)
	{
		return smartsKeys.get(index);
	}
	public static int indexOf(String key)
	{
		return smartsKeys.indexOf(key);
	}
}
