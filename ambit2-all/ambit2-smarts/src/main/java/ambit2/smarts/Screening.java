package ambit2.smarts;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

public class Screening {
	IsomorphismTester isoTester = new IsomorphismTester();
	ScreeningData querySD = new ScreeningData();
	IAtomContainer extractedQueryAC;
	Fingerprinter fp = new Fingerprinter();
	SmartsToChemObject convertor;
	SmartsParser parser = new SmartsParser();

	boolean FlagUseStrKeys = true;
	int nKeys;

	List<String> smartsKeys;
	List<IQueryAtomContainer> smartsQueries = new ArrayList<IQueryAtomContainer>();
	List<List<QuerySequenceElement>> sequences = new ArrayList<List<QuerySequenceElement>>();

	/**
	 * Screening by fingerprints and structural keys
	 */
	public Screening(IChemObjectBuilder builder) throws Exception {
		FlagUseStrKeys = true;
		getStandardKeys();
		prepareKeySequences();
		convertor = new SmartsToChemObject(builder);
	}

	/**
	 * Fingerprints screening is on always
	 * 
	 * @param useStrKeys
	 *            whether to screen by structural keys
	 */
	public Screening(boolean useStrKeys) throws Exception {
		FlagUseStrKeys = useStrKeys;
		if (FlagUseStrKeys) {
			getStandardKeys();
			prepareKeySequences();
		}
	}

	/**
	 * structural keys by external file
	 * 
	 * @param externalSmartsKeys
	 */
	public Screening(List<String> externalSmartsKeys) {
		FlagUseStrKeys = true;
		smartsKeys = externalSmartsKeys;
		nKeys = smartsKeys.size();
		prepareKeySequences();
	}

	/**
	 * obtained by {@link SmartsParser} - might not work by other
	 * QueryAtomContainer
	 * 
	 * @param query
	 *            search for this query
	 */
	public void setQuery(IQueryAtomContainer query) throws Exception {
		extractedQueryAC = convertor.extractAtomContainer(query);
		querySD = getScreeningDataForTarget(extractedQueryAC);

	}

	/**
	 * PreScreening - run this after the query is set
	 * 
	 * @param targetSD
	 * @return
	 */
	public boolean checkTarget(ScreeningData targetSD) {
		if (!bitSetCheck(querySD.fingerprint, targetSD.fingerprint))
			return (false);

		if (FlagUseStrKeys) {
			if (!bitSetCheck(querySD.structureKeys, targetSD.structureKeys))
				return (false);
		}
		return true;
	}

	/**
	 * Every bit set on in the query shouldbe present inthe target but the
	 * target can have more bits set on compared to the query
	 * 
	 * @param query
	 * @param target
	 * @return
	 */
	public boolean bitSetCheck(BitSet query, BitSet target) {
		for (int i = 0; i < query.size(); i++) {
			if (query.get(i)) {
				if (!target.get(i))
					return (false);
			}
		}
		return (true);
	}

	public boolean bitSetCheck(BitSet query, BitSet target, int nBits) {
		for (int i = 0; i < nBits; i++) {
			if (query.get(i)) {
				if (!target.get(i))
					return (false);
			}
		}
		return (true);
	}

	/**
	 * The {@link ScreeningData} for the target structure
	 * 
	 * @param ac
	 * @return
	 */
	public ScreeningData getScreeningDataForTarget(IAtomContainer ac)
			throws Exception {
		ScreeningData sd = new ScreeningData();
		sd.fingerprint = fp.getBitFingerprint(ac).asBitSet();
		if (FlagUseStrKeys)
			sd.structureKeys = getStructureKeyBits(ac);
		return sd;
	}

	public BitSet getStructureKeyBits(IAtomContainer ac) {
		BitSet keys = new BitSet(nKeys);
		boolean res;
		for (int i = 0; i < nKeys; i++) {
			isoTester.setSequence(smartsQueries.get(i), sequences.get(i));
			res = isoTester.hasIsomorphism(ac);
			keys.set(i, res);
			// System.out.println(smartsKeys.get(i) + "  " + res);
		}
		return (keys);
	}

	void getStandardKeys() throws Exception {
		SmartsScreeningKeys smartsScrKeys = new SmartsScreeningKeys();
		smartsKeys = smartsScrKeys.getKeys();
		nKeys = smartsScrKeys.nKeys;
	}

	void prepareKeySequences() {
		IQueryAtomContainer query;
		sequences.clear();

		for (int i = 0; i < nKeys; i++) {
			query = parser.parse(smartsKeys.get(i));

			// parser.setNeededDataFlags(); --> This should not be needed for
			// the key smarts queries
			isoTester.setQuery(query);
			List<QuerySequenceElement> sequence = isoTester
					.transferSequenceToOwner();
			sequences.add(sequence);
			smartsQueries.add(query);
		}
	}

	public String strKeysToString(BitSet bs) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < bs.size(); i++)
			if (bs.get(i))
				buf.append(smartsKeys.get(i) + ", ");
		return (buf.toString());
	}

	public String getBitSetString(BitSet bs) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < smartsKeys.size(); i++) {
			if (bs.get(i))
				buf.append("1");
			else
				buf.append("0");
		}
		return (buf.toString());
	}

	public BitSet stringToBitSet(String s) {
		BitSet bs = new BitSet(s.length());
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != '0')
				bs.set(i);
		}
		return (bs);
	}

	public String queryKeysToString() {
		StringBuffer buf = new StringBuffer();
		BitSet bs = querySD.structureKeys;
		for (int i = 0; i < bs.size(); i++)
			if (bs.get(i))
				buf.append(smartsKeys.get(i) + ", ");
		return (buf.toString());
	}
}
