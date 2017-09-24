package ambit2.similarity.test;

import java.net.URL;

import org.junit.Test;

import com.tdunning.math.stats.FloatHistogram;
import com.tdunning.math.stats.TDigest;

import ambit2.descriptors.simmatrix.SimilarityMatrix;
import ambit2.descriptors.simmatrix.SimilarityMatrixBitset;
import ambit2.descriptors.simmatrix.SimilarityMatrixDouble;
import junit.framework.Assert;

public class SimilarityMatrixTest {
	@Test
	public void testDoubleMatrix() throws Exception {
		SimilarityMatrixDouble matrix = new SimilarityMatrixDouble();
		matrix.setDelimiter("\t");
		URL resource = SimilarityMatrixTest.class.getClassLoader()
				.getResource("ambit2/similarity/test/testdoubledense.txt");
		Assert.assertNotNull(resource);
		TDigest histogram = matrix.createMatrix(resource.getFile(), true, 200, 0, -1);
		
		System.out.print(SimilarityMatrix.histogram2string(histogram));
		// 0.0:1 0.1:0 0.2:0 0.3:0 0.4:0 0.5:0 0.6:0 0.7:0 0.8:0 0.9:0 1.0:0
		// >1:44
		double[] h = new double[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 44 };
		
	}

	@Test
	public void testBitsetMatrix() throws Exception {
		SimilarityMatrixBitset matrix = new SimilarityMatrixBitset();
		matrix.setDelimiter(",");
		URL resource = SimilarityMatrixTest.class.getClassLoader()
				.getResource("ambit2/similarity/test/testbitsetsparse.csv");
		Assert.assertNotNull(resource);
		TDigest histogram = matrix.createMatrix(resource.getFile(), false, 0.25f, 0, -1);
		// 0.0:0 0.1:9 0.2:33 0.3:1 0.4:0 0.5:0 0.6:0 0.7:0 0.8:1 0.9:0 1.0:1
		System.out.print(SimilarityMatrix.histogram2string(histogram,10,true,false));
		double[] h = new double[] { 0, 9, 33, 1, 0, 0, 0, 0, 1, 0, 1, 0 };

	}

	@Test
	public void testhist() throws Exception {
		FloatHistogram h = new FloatHistogram(1E-100, 10);
		h.add(0);
		h.add(1E-100);
		h.add(0.2);
		h.add(0.8);
		h.add(0.81);
		h.add(200);
		h.add(1000);
		Assert.assertEquals(h.getBounds().length, h.getCounts().length);
		System.out.println("Bounds\tcount");
		int c = 0;
		for (int i = 0; i < h.getBounds().length; i++) {
			System.out.print(String.format("%e\t%s\t%s\n", h.getBounds()[i], h.getCounts()[i],
					(h.getCounts()[i] > 0 ? "<<<" : "")));
			c += h.getCounts()[i];
		}
		Assert.assertEquals(7, c);

	}

	@Test
	public void testdigest() throws Exception {
		final TDigest h = TDigest.createDigest(100);
		for (int i = 0; i < 500000000; i++)
			h.add(i*Math.random());

		System.out.println(h.centroids());
		for (double q : new double[] { 0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0 }) {
			double q2 = h.quantile(q);
			
			System.out.println(String.format("at\t%3.2f\t%3.2f\t%3.2f", q, q2, h.cdf(q2)));
		}

		System.out.println(String.format("[%e\t%e]", h.getMin(), h.getMax()));

	}
}
