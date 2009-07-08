package ambit2.smarts.processors;

import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.core.processors.structure.MoleculeReader;

public class SMARTSPropertiesGeneratorTest {
	@Test
	public void test() throws Exception {
		MoleculeReader mr = new MoleculeReader();
		SMARTSPropertiesGenerator gen = new SMARTSPropertiesGenerator();
		RawIteratingSDFReader reader = new RawIteratingSDFReader(
				new InputStreamReader(getClass().getClassLoader().getResourceAsStream("test.sdf")));
		while (reader.hasNext()) {
			IStructureRecord record = reader.nextRecord();
			IAtomContainer c = mr.process(record);
			record = gen.process(record);
			System.out.println( record.getProperty(gen.getProperty()));
			Assert.assertEquals(c.getProperty("EXPECTED"), record.getProperty(gen.getProperty()));
			Assert.assertNotNull(c.getProperty("EXPECTED"));
			
			//0210,0210,
			//0110,03310502,02210502,033206050102,033206060001,02210600,02210600,043206060003,03210603,04310603,0210,03210603,03210603,044206060003,0110,044206060001,0110,03310601,0110,02210601,044206050102,0110,04410502,0110,0430,0210,0220,0110,

			
		}
		reader.close();
	}
}
