package ambit2.descriptors.simmatrix;

import ambit2.similarity.measure.EuclideanDistance;

public class SimilarityMatrixDouble extends SimilarityMatrix<double[]> {
	public SimilarityMatrixDouble() {
		super();
		setDistance(new EuclideanDistance());
	}

	@Override
	protected L parseLineDense(String line, long record) throws Exception {
		String[] tokens = line.split(delimiter);
		L l = new L();
		l.setKey(tokens[0]);
		l.setId(Long.toString(record));
		double[] bs = new double[tokens.length - 1];
		for (int i = 1; i < tokens.length; i++) {
			bs[i - 1] = Double.parseDouble(tokens[i]);
		}
		l.setValue(bs);
		return l;
	}

	@Override
	protected String getSimMatrixFormat() {
		return super.getSimMatrixFormat();
		// return "%s\t%s\t%e\n";
	}

	@Override
	protected boolean accept(float t, float threshold) {
		return (t <= threshold);
	}

	@Override
	protected L parseLineSparse(String line, long record) throws Exception {
		throw new Exception("Sparse format not supported");
	}
}
