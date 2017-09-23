package ambit2.similarity.test;

import java.net.URL;

import org.junit.Test;

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
		long[] histogram = matrix.createMatrix(resource.getFile(), true, 200, 0, -1);
		
		for (int i = 0; i < histogram.length - 1; i++)
			System.out.print(String.format("%3.1f:%s\t", i * 0.1, histogram[i]));
		System.out.print(String.format(">1:%s\t", histogram[histogram.length - 1]));
		// 0.0:1 0.1:0 0.2:0 0.3:0 0.4:0 0.5:0 0.6:0 0.7:0 0.8:0 0.9:0 1.0:0
		// >1:44
		double[] h = new double[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 44 };
		for (int i = 0; i < histogram.length; i++) {
			Assert.assertEquals(h[i], histogram[i], 1E-6);
		}
	}

	@Test
	public void testBitsetMatrix() throws Exception {
		SimilarityMatrixBitset matrix = new SimilarityMatrixBitset();
		matrix.setDelimiter(",");
		URL resource = SimilarityMatrixTest.class.getClassLoader()
				.getResource("ambit2/similarity/test/testbitsetsparse.csv");
		Assert.assertNotNull(resource);
		long[] histogram = matrix.createMatrix(resource.getFile(), false, 0.25f, 0, -1);
		// 0.0:0 0.1:9 0.2:33 0.3:1 0.4:0 0.5:0 0.6:0 0.7:0 0.8:1 0.9:0 1.0:1
		double[] h = new double[] { 0, 9, 33, 1, 0, 0, 0, 0, 1, 0, 1, 0 };
		for (int i = 0; i < histogram.length; i++) {
			// System.out.print(String.format("%3.1f:%s\t", i * 0.1,
			// histogram[i]));
			Assert.assertEquals(h[i], histogram[i], 1E-6);
		}

	}
}
