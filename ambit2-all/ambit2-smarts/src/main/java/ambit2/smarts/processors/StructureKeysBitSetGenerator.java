package ambit2.smarts.processors;

import java.util.BitSet;
import java.util.Vector;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.processors.structure.AtomConfigurator;
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
	protected AtomConfigurator cfg = new AtomConfigurator();
	protected CDKHueckelAromaticityDetector aromaticDetector = new CDKHueckelAromaticityDetector();
	protected boolean cleanKekuleBonds = true;
	public boolean isCleanKekuleBonds() {
		return cleanKekuleBonds;
	}
	public void setCleanKekuleBonds(boolean cleanKekuleBonds) {
		this.cleanKekuleBonds = cleanKekuleBonds;
	}
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
	protected synchronized void prepareKeySequences(Vector<String> keys, int nKeys)
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
		try {
			IAtomContainer ac = cfg.process(target);
			aromaticDetector.detectAromaticity(ac);
			return getStructureKeyBits(ac);
		} catch (CDKException x) {
			throw new AmbitException(x.getMessage());
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	
	protected synchronized BitSet getStructureKeyBits(IAtomContainer ac)
	{
		//quick workaround for aromatic compounds, to avoid matching non-aromatic keys
		//TODO remove this when isoTester/keys processing is fixed 
		//isoTester is fixed, but CDK isomorphism tester still needs the workaround, should be fixed in CDK nightly Mar 2010
		if (cleanKekuleBonds)
			for (IBond bond : ac.bonds()) 
				if (bond.getFlag(CDKConstants.ISAROMATIC)) bond.setOrder(Order.SINGLE);
		//end of the workaround
		
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
