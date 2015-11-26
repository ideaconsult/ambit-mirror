package ambit2.smarts.processors;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

import ambit2.core.helper.CDKHueckelAromaticityDetector;
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
	protected static List<IQueryAtomContainer> smartsQueries = null;	
	protected static List<List<QuerySequenceElement>> sequences = null; 
	protected static List<String> smartsKeys; //not really needed, except for printing structure keys
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
	
	public StructureKeysBitSetGenerator() throws Exception {
		if ((smartsQueries==null) || (sequences==null)) {
			smartsQueries = new ArrayList<IQueryAtomContainer>();
			sequences = new ArrayList<List<QuerySequenceElement>>();
			SmartsScreeningKeys smartsScrKeys = new SmartsScreeningKeys(); 
			nKeys = smartsScrKeys.nKeys;
			prepareKeySequences(smartsScrKeys.getKeys(),smartsScrKeys.nKeys);			
		}

	}	
	public StructureKeysBitSetGenerator(List<String> externalSmartsKeys)	throws Exception
	{
		setSmartsKeys(externalSmartsKeys);

	}	
	public void setSmartsKeys(List<String> smartsKeys) {
		prepareKeySequences(smartsKeys,smartsKeys.size());		
	}
	protected synchronized void prepareKeySequences(List<String> keys, int nKeys)
	{
		smartsKeys = keys; 
		IQueryAtomContainer query;
		sequences.clear();
		smartsQueries.clear();
		SmartsParser parser = new SmartsParser();
		for (int i = 0; i < nKeys; i++)
		{
			query = parser.parse(smartsKeys.get(i));
			
			//parser.setNeededDataFlags();       --> This should not be needed for the key smarts queries
			isoTester.setQuery(query);
			List<QuerySequenceElement> sequence = isoTester.transferSequenceToOwner();
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
		for (IBond bond : ac.bonds()) 
			if (bond.getFlag(CDKConstants.ISAROMATIC)) {
				for (IAtom a : bond.atoms()) a.setFlag(CDKConstants.ISAROMATIC,true); 
				//in e.g. triazole the atoms are not set as aromatics, but bonds are!
				if (cleanKekuleBonds)
					bond.setOrder(Order.SINGLE);
			}
		//end of the workaround
		
		
		BitSet keys = new BitSet(nKeys);
		boolean res;
		for (int i = 0; i < nKeys; i++) 
		{
			isoTester.setSequence(smartsQueries.get(i), sequences.get(i));
			res = isoTester.hasIsomorphism(ac);
			keys.set(i, res);
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
