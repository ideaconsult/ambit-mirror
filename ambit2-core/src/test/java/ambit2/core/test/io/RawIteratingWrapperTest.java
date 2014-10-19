package ambit2.core.test.io;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.FileInputState;
import ambit2.core.io.IteratingDelimitedFileReader;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.core.io.RawIteratingWrapper;
import ambit2.core.io.ToxcastAssayReader;
import ambit2.core.io.dx.DXParser;
import ambit2.core.processors.StructureNormalizer;

public class RawIteratingWrapperTest {
	@Test
	public void test() throws Exception {
		IIteratingChemObjectReader reader = FileInputState.getReader(
				RawIteratingWrapperTest.class.getClassLoader().getResourceAsStream("ambit2/core/data/io/test.txt")
				, "test.txt");
		
		Assert.assertTrue(reader != null);
		Assert.assertTrue(reader instanceof IteratingDelimitedFileReader);

		RawIteratingWrapper wrapper = new RawIteratingWrapper(reader);
		int count = 0;
		while(wrapper.hasNext()) {
			IStructureRecord record = (IStructureRecord) wrapper.next();
			count++;
		}
		Assert.assertEquals(11,count);
		wrapper.close();
	}
	
	@Test
	public void testToxcast() throws Exception {
		IIteratingChemObjectReader reader = FileInputState.getReader(
				RawIteratingWrapperTest.class.getClassLoader().getResourceAsStream("ambit2/core/data/toxcast/test.txt")
				, "test.txt");
		
		Assert.assertTrue(reader != null);
		Assert.assertTrue(reader instanceof IteratingDelimitedFileReader);
		Assert.assertTrue(reader instanceof ToxcastAssayReader);

		int count = 0;
		while(reader.hasNext()) {
			IMolecule record = (IMolecule) reader.next();
			Assert.assertEquals(10,record.getProperties().size());
			Iterator<Object> keys = record.getProperties().keySet().iterator();
			while (keys.hasNext()) {
				Assert.assertNotNull(record.getProperty(keys.next()));
			}
			count++;
		}
		Assert.assertEquals(4,count);
		reader.close();
	}	
	
	@Test
	public void testDX() throws Exception {
		InputStream in = RawIteratingWrapperTest.class.getClassLoader().getResourceAsStream("ambit2/core/data/dx/predictions.sdf");
		RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
		reader.setReference(LiteratureEntry.getInstance("predictions.sdf"));
		Assert.assertTrue(reader != null);
		
		DXParser dxParser = new DXParser();

		StructureNormalizer normalizer = new StructureNormalizer();
		int count = 0;
		while(reader.hasNext()) {
			IStructureRecord record = (IStructureRecord) reader.next();
			IStructureRecord normalized = normalizer.process(record);
			
			System.out.println(normalized.getContent());
			int rr = 0;
			for (Property p : normalized.getProperties()) {
				rr++;
				System.out.println(String.format(">>\t%s\t=%s", p.getName(),normalized.getProperty(p)));
				Assert.assertNotNull(normalized.getProperty(p));
			}
			Assert.assertEquals(51,rr);
			/*
			normalized = dxParser.process(normalized);
			for (Property p : normalized.getProperties()) {
				System.out.println(String.format("DX>>\t%s\t=%s", p.getName(),normalized.getProperty(p)));
				Assert.assertNotNull(normalized.getProperty(p));
			}
			*/
			count++;
		}
		Assert.assertEquals(1,count);
		reader.close();
	}
	
	@Test
	public void testPubChemSubstance() throws Exception {
		InputStream in = RawIteratingWrapperTest.class.getClassLoader().getResourceAsStream("ambit2/core/pubchem/tox21_excerpt.sdf");
		RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
		reader.setReference(LiteratureEntry.getInstance("tox21.sdf"));
		Assert.assertTrue(reader != null);
		StructureNormalizer normalizer = new StructureNormalizer();		
		int count = 0;
		int sid = 0;
		while(reader.hasNext()) {
			IStructureRecord record = (IStructureRecord) reader.next();

			IStructureRecord normalized = normalizer.process(record);
			for (Property p: normalized.getProperties()) {
				if ("PUBCHEM_SID".equals(p.getName())) {
						Assert.assertNotNull(normalized.getProperty(p));
						sid++;
				} else if ("PUBCHEM Name".equals(p.getName()))
					Assert.assertNotNull(normalized.getProperty(p));
				else if ("DSSTox_GSID".equals(p.getName()))
					Assert.assertNotNull(normalized.getProperty(p));
				else if ("CASRN".equals(p.getName()))
					Assert.assertNotNull(normalized.getProperty(p));
				else if ("DSSTox_RID".equals(p.getName()))
					Assert.assertNotNull(normalized.getProperty(p));	
			}
			count++;
		}
		Assert.assertEquals(3,sid);
		Assert.assertEquals(3,count);
		reader.close();
	}
	
	public static void main(String[] args) {
		if (args==null || args.length==0) System.exit(-1);
		
		File file = new File(args[0]);
		RawIteratingSDFReader reader = null;
		try {
			reader = new RawIteratingSDFReader(new FileReader(file));
			int count = 0;
			
			long start = System.currentTimeMillis();
			long now = System.currentTimeMillis();
			long startRead = start;
			while (reader.hasNext()) {
				IStructureRecord mol = reader.nextRecord();
				
			//System.out.println(mol.getContent());
				
				count++;
				if ((count % 1000)==0) {
					now = System.currentTimeMillis();
					System.out.print(count);
					System.out.print("\t");
					System.out.println((now-start)/100.0);
					start = now;		
				}
			}
			now = System.currentTimeMillis();
			System.out.println(count);
			System.out.print(now-startRead);
			System.out.println(" msec");
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try {if (reader != null) reader.close();} catch(Exception x) {}
		}
		
	}
}
