package ambit2.smarts.query;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsParser;

public class FastSmartsMatcher extends AbstractSmartsPattern<IAtomContainer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -437122688052917294L;
	protected IsomorphismTester isoTester = new IsomorphismTester();
	protected SmartsParser sp = new SmartsParser();
	protected QueryAtomContainer query = null;
	
	public FastSmartsMatcher() {

	}
	public FastSmartsMatcher(String smarts,boolean negate) throws SMARTSException {
		setSmarts(smarts);
		setNegate(negate);
	}
	public QueryAtomContainer getQuery() {
		return query;
	}

	public void setQuery(QueryAtomContainer query) {
		this.query = query;
	}

	public IAtomContainer getMatchingStructure(IAtomContainer mol)
			throws SMARTSException {
		throw new UnsupportedOperationException("Not implemented");
	}

	public IAtomContainer getObjectToVerify(IAtomContainer mol) {
		return mol;
	}
	@Override
	public void setSmarts(String smarts) throws SMARTSException {
		query  = sp.parse(smarts);
		sp.setNeededDataFlags();
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals("")) {
			query = null;
			super.setSmarts(null);
			throw new SMARTSException("Smarts Parser errors:\n" + errorMsg);			
		} else super.setSmarts(smarts);						 
	}

	public int hasSMARTSPattern(IAtomContainer mol) throws SMARTSException {
		System.out.println(getClass().getName());
		isoTester.setQuery(query);
		sp.setSMARTSData(mol);
		return isoTester.hasIsomorphism(mol)?1:0;
	}

	public void useMOEvPrimitive(boolean flag)
			throws UnsupportedOperationException {

		throw new UnsupportedOperationException("useMOEvPrimitive");
		
	}

}
