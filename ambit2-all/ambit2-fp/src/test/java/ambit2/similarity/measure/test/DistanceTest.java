package ambit2.similarity.measure.test;

import java.util.BitSet;

import org.junit.Test;

import ambit2.similarity.measure.EuclideanDistance;
import ambit2.similarity.measure.FingerprintDistance;
import ambit2.similarity.measure.HammingDistance;
import ambit2.similarity.measure.LevensteinDistance;
import junit.framework.Assert;

public class DistanceTest {

	@Test
	public void testHammingDistance() throws Exception {
		HammingDistance d = new HammingDistance();
		Assert.assertEquals(3.0, d.getDistance(new int[] { 0, 1, 3, 2, 8 }, new int[] { 1, 1, 6, 2, 4 }), 1E-6);
		Assert.assertEquals(3.0, d.getDistance(new int[] { 1, 1, 6, 2, 4 }, new int[] { 0, 1, 3, 2, 8 }), 1E-6);
		Assert.assertEquals(0.0, d.getDistance(new int[] { 0, 1, 3, 2, 8 }, new int[] { 0, 1, 3, 2, 8 }), 1E-6);
	}

	@Test
	public void testLevensteinDistance() throws Exception {
		LevensteinDistance d = new LevensteinDistance();
		Assert.assertEquals(2.0, d.getDistance("GUMBO", "GAMBOL"), 0);
	}

	@Test
	public void testFingerprintDistance() throws Exception {
		FingerprintDistance d = new FingerprintDistance();
		BitSet bs1 = new BitSet();
		bs1.set(1);
		bs1.set(2);
		BitSet bs2 = new BitSet();
		bs2.set(1);
		bs2.set(3);
		Assert.assertEquals(1.0/3.0, d.getDistance(bs1, bs2),1E-6);
	}

	@Test
	public void testEuclideanDistance() throws Exception {
		EuclideanDistance d = new EuclideanDistance();
		double[] v1 = new double[] {3.0,0.0};
		double[] v2 = new double[] {4.0,0.0};
		Assert.assertEquals(1.0f, d.getDistance(v1,v2));
	}
}
