package ambit2.core.data;

import java.util.Map;
import java.util.Map.Entry;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.IBitFingerprint;
import org.openscience.cdk.fingerprint.ICountFingerprint;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;

public class HashIntDescriptorResult extends HashDescriptorResult<Integer>
		implements IFingerprinter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8497822347752458361L;
	protected int maxLevels = -1;
	public String factory;

	public String getFactory() {
		return factory;
	}

	public void setFactory(String factory) {
		this.factory = factory;
	}

	public int getMaxLevels() {
		return maxLevels;
	}

	public void setMaxLevels(int maxLevels) {
		this.maxLevels = maxLevels;
	}

	public HashIntDescriptorResult(Map<String, Integer> map) throws Exception {
		super(map);
	}

	@Override
	public IBitFingerprint getBitFingerprint(IAtomContainer container)
			throws CDKException {
		throw new CDKException("not implemented yet");
	}

	@Override
	public ICountFingerprint getCountFingerprint(IAtomContainer container)
			throws CDKException {
		throw new CDKException("not implemented yet");
	}

	@Override
	public Map<String, Integer> getRawFingerprint(IAtomContainer container)
			throws CDKException {
		return results;
	}

	@Override
	public int getSize() {
		return length();
	}
	/**
	 * Used for db storage
	 * @return
	 */
	public String expandedString() {
		if (results == null)
			return null;
		StringBuilder b = new StringBuilder();
		
		for (Entry<String, Integer> e : results.entrySet()) {
			String key = normalizeKey(e.getKey());

			b.append(key);
			b.append(String.format(" %s%d ", key, e.getValue()));
		}
		return b.toString();
	}
	
	public String normalizeKey(String key) {
		
		return key = key.replace(".", "").replace("_","");
	}
	public String keys2String(int maxlen) {
		if (results == null)
			return null;
		StringBuilder b = new StringBuilder();
		
		for (Entry<String, Integer> e : results.entrySet()) {
			if ((maxlen>0) && e.getValue()>maxlen) continue;
			String key = normalizeKey(e.getKey());
			b.append(key);
			b.append(" ");
			b.append(String.format("%s%d", key, e.getValue()));
			b.append(" ");
		}
		return b.toString();
	}

	public String asBooleanQuery(int maxlen) {
		if (results == null)
			return null;
		StringBuilder b = new StringBuilder();
		double max = 0;
		for (Entry<String, Integer> e : results.entrySet()) if (e.getValue()>max) max = e.getValue();
		max = max / 2;
		
		for (Entry<String, Integer> e : results.entrySet()) {
			String importance = " ";
			if (e.getValue()>max) importance = " <"; //more common atomtypes, less important, but still there
			String key = normalizeKey(e.getKey());
			b.append(importance);
			b.append(key);
			b.append(String.format(" %s%s%d ", ">",key, e.getValue()));
		}
		return b.toString();
	}
	
	@Override
	public String toString() {
		return expandedString();
	}

}
