package ambit2.descriptors.simmatrix;

import java.util.BitSet;
import java.util.logging.Level;

import ambit2.similarity.measure.FingerprintDistance;

public class SimilarityMatrixBitset extends SimilarityMatrix<BitSet> {
	public SimilarityMatrixBitset() {
		super();
		setDistance(new FingerprintDistance());
	}

	@Override
	protected boolean accept(float t, double threshold) {
		return (t >= threshold);
	}

	protected L parseLineDense(String line, long record) throws Exception {
		String[] tokens = line.split(delimiter);
		L l = new L();
		l.setKey(tokens[0]);
		l.setId(Long.toString(record));
		BitSet bs = new BitSet();
		for (int i = 1; i < tokens.length; i++) {
			if ("1".equals(tokens[i]))
				bs.set(i - 1);
		}
		l.setValue(bs);
		return l;
	}

	@Override
	protected L parseLineSparse(String line, long record) throws Exception {
		String[] tokens = line.split(delimiter);
		L l = new L();
		l.setKey(tokens[0]);
		l.setId(Long.toString(record));
		BitSet bs = new BitSet();
		for (int i = 1; i < tokens.length; i++)
			try {
				int key = Integer.parseInt(tokens[i]);
				// System.out.println(key);
				bs.set(Math.abs(key));
				Integer v = bitmap.get(key);
				if (v == null) {
					v = bitmap.size();
					bitmap.put(key, v);
				}
				bs.set(v);

			} catch (Exception x) {
				logger.log(Level.WARNING, x.getMessage());
			}
		l.setValue(bs);
		return l;
	}

}
