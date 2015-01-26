package ambit2.model.numeric;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.Assert;

import org.junit.Test;

import Jama.Matrix;

public class DataCoverageLeverageTest {
    @Test
    public void testRoundTrip() throws Exception {
	DataCoverageLeverageHatMatrix predictor = new DataCoverageLeverageHatMatrix();
	Matrix matrix = Matrix.random(2300, 1);
	predictor.build(matrix);

	ByteArrayOutputStream out = new ByteArrayOutputStream();

	// serialize model
	ObjectOutputStream oos = new ObjectOutputStream(out);
	oos.writeObject(predictor);
	oos.flush();
	oos.close();

	byte[] content = out.toByteArray();

	// System.out.println(String.format(" %d bytes", content.length));
	InputStream in = new ByteArrayInputStream(content);
	ObjectInputStream ois = new ObjectInputStream(in);
	Object o = ois.readObject();
	Assert.assertTrue(o instanceof DataCoverageLeverageHatMatrix);
	DataCoverageLeverageHatMatrix l = (DataCoverageLeverageHatMatrix) o;
	Assert.assertEquals(predictor.getAppDomainMethodType(), l.getAppDomainMethodType());
	Assert.assertEquals(predictor.getPThreshold(), l.getPThreshold());
	Assert.assertEquals(predictor.threshold, l.threshold);
	Assert.assertEquals(predictor.hat.getRowDimension(), l.hat.getRowDimension());
	Assert.assertEquals(predictor.XTX1.getColumnDimension(), l.XTX1.getColumnDimension());

	for (int i = 0; i < predictor.hat.getRowDimension(); i++)
	    for (int j = 0; j < predictor.hat.getColumnDimension(); j++)
		Assert.assertEquals(predictor.hat.get(i, j), l.hat.get(i, j), 1E-6);

	for (int i = 0; i < predictor.XTX1.getRowDimension(); i++)
	    for (int j = 0; j < predictor.XTX1.getColumnDimension(); j++)
		Assert.assertEquals(predictor.XTX1.get(i, j), l.XTX1.get(i, j), 1E-6);

    }
}
