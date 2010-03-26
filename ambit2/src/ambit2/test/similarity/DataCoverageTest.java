package ambit2.domain.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import Jama.Matrix;
import ambit2.model.numeric.DataCoverage;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public abstract class DataCoverageTest<DATA,DC extends DataCoverage<DATA>> {
	protected DC method;
	protected DATA dataTrain;
	protected DATA dataTest;
	protected abstract DC createMethod();
	protected abstract DATA initTrainingData();
	protected abstract DATA initTestData();

	
	@Before
	public void setup() throws Exception {
		dataTrain = initTrainingData();
		dataTest = initTestData();
		Assert.assertNotNull(dataTrain);
		Assert.assertNotNull(dataTest);
	}
	@Test
	public void testBuild() throws Exception  {
		if(method == null) method = createMethod();
		Assert.assertTrue(method.build(dataTrain));
		double[] resultsTrain = method.predict(dataTrain);
		Assert.assertNotNull(resultsTrain);
		int[] domain = method.getDomain(resultsTrain);
		for (double d : domain) Assert.assertEquals(0.0,d); //all are in domain
		verifyTraining(dataTrain,resultsTrain);
		double[] resultsTest = method.predict(dataTest);
		Assert.assertNotNull(resultsTest);
		verifyTest(dataTest,resultsTest);
		domain = method.getDomain(resultsTest);
		for (int i=0; i< domain.length;i++)
			if (i==(domain.length-1))
				Assert.assertEquals(1.0,domain[i],1E-10); //in
			else
				Assert.assertEquals(0.0,domain[i],1E-10); //out
	}
	
	public abstract void verifyTraining(DATA data,double[] results) throws Exception;
	public abstract void verifyTest(DATA data,double[] results) throws Exception;
	
	@Test
	public void testSerialize() throws Exception {
		if(method == null) method = createMethod();
		Assert.assertTrue(method.build(dataTrain));
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		 // serialize model
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(method);
		oos.flush();
		oos.close();	
		
		byte[] content = out.toByteArray();
		
		String encodedContent = Base64.encode(content);
		
		InputStream in = new ByteArrayInputStream(Base64.decode(encodedContent));
		ObjectInputStream ois =  new ObjectInputStream(in);
		
		Object coverage = ois.readObject();
		Assert.assertNotNull(coverage);
		verifySerialized(coverage);
				
	}
	public void verifySerialized(Object coverage) throws Exception {
		Assert.assertTrue(coverage instanceof DataCoverage);
	}
}
