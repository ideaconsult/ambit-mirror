package ambit2.similarity.measure;

public class EuclideanDistance implements IDistanceFunction<double[]> {
	private int len = -1;
	@Override
	public float getDistance(double[] object1, double[] object2) throws Exception {
		return getNativeComparison(object1, object2);

	}

	@Override
	public float getNativeComparison(double[] o1, double[] o2) throws Exception {
		try {
			if (len<=0) len = o1.length;
			double s = 0;
			for (int i = 0; i < len; i++)
				s += (o1[i] - o2[i]) * (o1[i] - o2[i]);
			return (float) Math.sqrt(s);
		} catch (Exception x) {
			throw x;
		}
	}

	@Override
	public String toString() {
		return String.format("Euclidean distance R%d",len);
	}
}
