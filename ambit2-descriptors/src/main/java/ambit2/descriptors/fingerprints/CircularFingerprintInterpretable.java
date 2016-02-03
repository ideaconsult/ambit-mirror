package ambit2.descriptors.fingerprints;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.CircularFingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;

public class CircularFingerprintInterpretable extends CircularFingerprinter implements ISparseFingerprint {
	@Override
	public Map<String, Integer> getRawFingerprint(IAtomContainer mol)
			throws CDKException {
		calculate(mol);
		final Map<String, Integer> map = new TreeMap<String, Integer>();
		
		for (int i=0; i < getFPCount(); i++) {
			FP fp = getFP(i);
			String key = Integer.toString(fp.hashCode);
			if (map.containsKey(key)) {
				map.put(key, map.get(key) + 1);
			} else
				map.put(key, 1);
		}
		return map;
	}
	
	public static String fp2string(IAtomContainer mol, FP fp, int count) {
		StringBuilder b = new StringBuilder();
		b.append("{ ");
		b.append("\"hash\":");
		b.append(fp.hashCode);
		b.append(",");
		b.append("\"value\":");
		b.append(String.format("%d",fp.iteration));
		b.append(",");
		b.append("\"count\":");
		b.append(String.format("%d",count));
		b.append(",");
		b.append("\"encoding\":\"");
		for (int i=0; i < fp.atoms.length; i++) {
			b.append(mol.getAtom(fp.atoms[i]).getSymbol());
			//b.append(" ");
		}
		b.append("\"");
		b.append(" }");
		return b.toString();
	}
	@Override
	public Iterable<Object> getSparseFingerprint(IAtomContainer mol)
			throws CDKException {
		calculate(mol);
		final Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
		final Map<Integer, FP> mapFP = new TreeMap<Integer, FP>();
		
		for (int i=0; i < getFPCount(); i++) {
			FP fp = getFP(i);
			Integer key = fp.hashCode;
			if (map.containsKey(key)) {
				map.put(key, map.get(key) + 1);
			} else
				map.put(key, 1);
			mapFP.put(key,fp);
			//we don't care about hash colisions!
		}
		List sparse = new ArrayList<>();
		
		Iterator<Entry<Integer,FP>> i = mapFP.entrySet().iterator();
		while (i.hasNext()) {
			Entry<Integer,FP> e = i.next();
			sparse.add(fp2string(mol,e.getValue(), map.get(e.getKey())));
		}
		return sparse;
	}
}
