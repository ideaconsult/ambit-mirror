package ambit2.descriptors.test;

import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.LengthOverBreadthDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.WHIMDescriptor;

import ambit2.core.io.FileInputState;
import ambit2.descriptors.SizeDescriptor;
import ambit2.descriptors.SpherosityDescriptor;

/**
 * Test for descriptors, requiring 3D coordinates. Some descriptors fail to recognise 3D coordinates are present
 * @author nina
 *
 */
public class Descriptors3DTest {
	@Test
	public void testWHIMDescriptor() throws Exception {
		testDescriptor(new WHIMDescriptor());
	}
	@Test
	public void testSpherosityDescriptor() throws Exception {
		testDescriptor(new SpherosityDescriptor());
	}
	@Test
	public void testSizeDescriptor() throws Exception {
		testDescriptor(new SizeDescriptor());
	}	
	@Test
	public void testLengthOverBreadthDescriptor() throws Exception {
		testDescriptor(new LengthOverBreadthDescriptor());
	}

	//succeeds if no exception is thrown
	public void testDescriptor(IMolecularDescriptor descriptor) throws Exception {
		InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/descriptors/3d/test.sdf");
		IIteratingChemObjectReader reader = FileInputState.getReader(
				in,"test.sdf");

		while (reader.hasNext()) {
			Object next = reader.next();
			Assert.assertTrue(next instanceof IAtomContainer);
			DescriptorValue value = descriptor.calculate((IAtomContainer)next);
		}
		in.close();
	}
}
