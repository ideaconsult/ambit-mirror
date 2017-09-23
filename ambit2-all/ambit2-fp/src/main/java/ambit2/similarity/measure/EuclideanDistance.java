package ambit2.similarity.measure;

public class EuclideanDistance implements IDistanceFunction<double[]> {

	@Override
	public float getDistance(double[] object1, double[] object2) throws Exception {
		return getNativeComparison(object1, object2);

	}

	@Override
	public float getNativeComparison(double[] o1, double[] o2) throws Exception {
		double s = 0;
		for (int i = 0; i < o1.length; i++)
			s += (o1[i] - o2[i]) * (o1[i] - o2[i]);
		return (float) Math.sqrt(s);
	}

}
