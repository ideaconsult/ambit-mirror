package ambit2.smarts.processors;

import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.smarts.query.FastSmartsMatcher;

/**
 * 
 * @author nina
 *
 */
public class SMARTSPropertiesGeneratorTest {
	protected FastSmartsMatcher match = new FastSmartsMatcher();
	@Test
	public void test() throws Exception {
		MoleculeReader mr = new MoleculeReader();
		SMARTSPropertiesGenerator gen = new SMARTSPropertiesGenerator();
		RawIteratingSDFReader reader = new RawIteratingSDFReader(
				new InputStreamReader(getClass().getClassLoader().getResourceAsStream("test.sdf")));
		while (reader.hasNext()) {
			
			IStructureRecord record = reader.nextRecord();
			IAtomContainer c = mr.process(record);
			if (c.getAtomCount()<=2 ) continue;
		
			//no SMARTSProp assigned, will be generated on the fly
			Assert.assertNull(c.getProperty(gen.getProperty()));
			testMatch(c);
			
			String[] properties = new String[] {
					"0110,03310503,02210503,033206050203,033206060102,02210601,02210601,043206060001,03210600,04310600,0210,03210600,03210600,044206060001,0110,044206060102,0110,03310602,0110,02210602,044206050203,0110,04410503,0110,0430,0210,0220,0110,",
					"0110,03310502,02210502,033206050102,033206060001,02210600,02210600,043206060003,03210603,04310603,0210,03210603,03210603,044206060003,0110,044206060001,0110,03310601,0110,02210601,044206050102,0110,04410502,0110,0430,0210,0220,0110,",
					"0110,03310500,02210500,033205060002,033206060102,02210601,02210601,043206060103,03210603,04310603,0210,03210603,03210603,044206060103,0110,044206060102,0110,03310602,0110,02210602,044205060002,0110,04410500,0110,0430,0210,0220,0110,"
			};
			for (String prop : properties) {
				c = mr.process(record); //get the record anew
				c.removeProperty(gen.getProperty());
				c.setProperty(gen.getProperty(), prop);
				Assert.assertNotNull(c.getProperty(gen.getProperty()));
				testMatch(c);
			}
			
			
			c = mr.process(record); //get the record anew
			record = gen.process(record);  
			Assert.assertNotNull(record.getRecordProperty(gen.getProperty()));
			c.setProperty(gen.getProperty(),record.getRecordProperty(gen.getProperty()) );
			Assert.assertNotNull(c.getProperty(gen.getProperty()));
			testMatch(c);
			
		}
		reader.close();
	}
	
	protected void testMatch(IAtomContainer c) throws Exception {
		match.setSmarts("[r5;r6][r6;r6]CC[r6;r6]=C");
		Assert.assertTrue(match.hasSMARTSPattern(c)>0);
		
		match.setSmarts("[R1][R2][R2][R1][R1][R2]");
		Assert.assertTrue(match.hasSMARTSPattern(c)>0);		
		
		match.setSmarts("O[r6][r6;!r5;R2]F");
		Assert.assertTrue(match.hasSMARTSPattern(c)>0);		
		
		match.setSmarts("F[r5;r6]");
		Assert.assertTrue(match.hasSMARTSPattern(c)==0);	
	}
	
	protected void testMatchAromatic(IAtomContainer c) throws Exception {
		match.setSmarts("[r6]");
		Assert.assertTrue(match.hasSMARTSPattern(c)>0);
		
		match.setSmarts("c1ccccc1");
		Assert.assertTrue(match.hasSMARTSPattern(c)>0);		
		
		match.setSmarts("[r5]");
		Assert.assertTrue(match.hasSMARTSPattern(c)==0);	
	}	
	
	@Test
	public void testAromatic() throws Exception {
		MoleculeReader mr = new MoleculeReader();
		AtomConfigurator config = new AtomConfigurator();
		SMARTSPropertiesGenerator gen = new SMARTSPropertiesGenerator();
		RawIteratingSDFReader reader = new RawIteratingSDFReader(
				new InputStreamReader(getClass().getClassLoader().getResourceAsStream("benzene.sdf")));
		while (reader.hasNext()) {
			
			IStructureRecord record = reader.nextRecord();
			
			IAtomContainer c = config.process(mr.process(record));
			CDKHueckelAromaticityDetector.detectAromaticity(c);
			if (c.getAtomCount()<=2 ) continue;
		
			//no SMARTSProp assigned, will be generated on the fly
			Assert.assertNull(c.getProperty(gen.getProperty()));
			testMatchAromatic(c);
			
			//get the record anew, and set the property
			c = mr.process(record);
			record = gen.process(record);  
			Assert.assertNotNull(record.getRecordProperty(gen.getProperty()));
			c.setProperty(gen.getProperty(),record.getRecordProperty(gen.getProperty()) );
			Assert.assertNotNull(c.getProperty(gen.getProperty()));
			testMatchAromatic(c);

			
		}
		reader.close();
	}
	
}
