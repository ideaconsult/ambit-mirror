package ambit2.core.test.io;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import junit.framework.Assert;

import org.apache.commons.csv.CSVFormat;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.FileInputState;
import ambit2.core.io.RawIteratingCSVReader;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.core.io.RawIteratingWrapper;
import ambit2.core.io.dx.DXParser;
import ambit2.core.processors.StructureNormalizer;
import ambit2.core.processors.structure.MoleculeReader;

public class RawIteratingWrapperTest {
	@Test
	public void test() throws Exception {
		IIteratingChemObjectReader reader = FileInputState.getReader(
				RawIteratingWrapperTest.class.getClassLoader()
						.getResourceAsStream("ambit2/core/data/io/test.txt"),
				"test.txt");

		Assert.assertTrue(reader != null);
		// Assert.assertTrue(reader instanceof IteratingDelimitedFileReader);

		RawIteratingWrapper wrapper = new RawIteratingWrapper(reader);
		int count = 0;
		while (wrapper.hasNext()) {
			IStructureRecord record = (IStructureRecord) wrapper.next();
			count++;
		}
		Assert.assertEquals(11, count);
		wrapper.close();
	}

	@Test
	public void testCSVReader() throws Exception {
		RawIteratingCSVReader reader = new RawIteratingCSVReader(
				new InputStreamReader(RawIteratingWrapperTest.class
						.getClassLoader().getResourceAsStream(
								"ambit2/core/data/io/test.txt")), CSVFormat.TDF);
		
		MoleculeReader molreader = new MoleculeReader();
		try {
			int count = 0;
			while (reader.hasNext()) {
				IStructureRecord record = reader.nextRecord();
				if (record.getSmiles() != null && record.getInchi() != null) {
					Assert.assertNotNull(record.getContent());
					Assert.assertNotNull(record.getSmiles());
					Assert.assertNotNull(record.getInchi());
					Assert.assertNotNull(record.getInchiKey());
				}

				Object o = reader.next();
				Assert.assertNotNull(o);
				Assert.assertTrue(o instanceof IStructureRecord);
				
				IAtomContainer mol = molreader.process((IStructureRecord) o);
				if (record.getSmiles() != null
						&& !"".equals(record.getSmiles()))
					Assert.assertTrue(
							String.format("Atoms %d", mol.getAtomCount()),
							mol.getAtomCount() > 0);
				count++;
			}
			Assert.assertEquals(11, count);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testToxcast() throws Exception {
		IIteratingChemObjectReader reader = FileInputState.getReader(
				RawIteratingWrapperTest.class.getClassLoader()
						.getResourceAsStream(
								"ambit2/core/data/toxcast/test.txt"),
				"test.txt");

		Assert.assertTrue(reader != null);
		// Assert.assertTrue(reader instanceof IteratingDelimitedFileReader);
		// Assert.assertTrue(reader instanceof ToxcastAssayReader);

		int count = 0;
		while (reader.hasNext()) {
			IAtomContainer record = (IAtomContainer) reader.next();
			Assert.assertEquals(10, record.getProperties().size());
			Iterator<Object> keys = record.getProperties().keySet().iterator();
			while (keys.hasNext()) {
				Assert.assertNotNull(record.getProperty(keys.next()));
			}
			count++;
		}
		Assert.assertEquals(4, count);
		reader.close();
	}

	@Test
	public void testDX() throws Exception {
		InputStream in = RawIteratingWrapperTest.class.getClassLoader()
				.getResourceAsStream("ambit2/core/data/dx/predictions.sdf");
		RawIteratingSDFReader reader = new RawIteratingSDFReader(
				new InputStreamReader(in));
		reader.setReference(LiteratureEntry.getInstance("predictions.sdf"));
		Assert.assertTrue(reader != null);

		DXParser dxParser = new DXParser();

		StructureNormalizer normalizer = new StructureNormalizer();
		int count = 0;
		while (reader.hasNext()) {
			IStructureRecord record = (IStructureRecord) reader.next();
			IStructureRecord normalized = normalizer.process(record);

			//System.out.println(normalized.getContent());
			int rr = 0;
			for (Property p : normalized.getRecordProperties()) {
				rr++;
				//System.out.println(String.format(">>\t%s\t=%s\t%s", p.getName(),normalized.getRecordProperty(p),p.getLabel()));
				Assert.assertNotNull(normalized.getRecordProperty(p));
				Assert.assertNotNull(p.getLabel());
				Assert.assertNotNull(p.getName());
				if (p.getName().startsWith("DX.")) {
					Assert.assertTrue(p.getLabel().startsWith("http://www.opentox.org/echaEndpoints.owl#"));
					//System.out.println(p.getAnnotations());
				}
			}
			Assert.assertEquals(27, rr);
			/*
			 * normalized = dxParser.process(normalized); for (Property p :
			 * normalized.getProperties()) {
			 * System.out.println(String.format("DX>>\t%s\t=%s",
			 * p.getName(),normalized.getProperty(p)));
			 * Assert.assertNotNull(normalized.getProperty(p)); }
			 */
			count++;
		}
		Assert.assertEquals(1, count);
		reader.close();
	}

	@Test
	public void testPubChemSubstance() throws Exception {
		InputStream in = RawIteratingWrapperTest.class.getClassLoader()
				.getResourceAsStream("ambit2/core/pubchem/tox21_excerpt.sdf");
		RawIteratingSDFReader reader = new RawIteratingSDFReader(
				new InputStreamReader(in));
		reader.setReference(LiteratureEntry.getInstance("tox21.sdf"));
		Assert.assertTrue(reader != null);
		StructureNormalizer normalizer = new StructureNormalizer();
		int count = 0;
		int sid = 0;
		while (reader.hasNext()) {
			IStructureRecord record = (IStructureRecord) reader.next();

			IStructureRecord normalized = normalizer.process(record);
			for (Property p : normalized.getRecordProperties()) {
				if ("PUBCHEM_SID".equals(p.getName())) {
					Assert.assertNotNull(normalized.getRecordProperty(p));
					sid++;
				} else if ("PUBCHEM Name".equals(p.getName()))
					Assert.assertNotNull(normalized.getRecordProperty(p));
				else if ("DSSTox_GSID".equals(p.getName()))
					Assert.assertNotNull(normalized.getRecordProperty(p));
				else if ("CASRN".equals(p.getName()))
					Assert.assertNotNull(normalized.getRecordProperty(p));
				else if ("DSSTox_RID".equals(p.getName()))
					Assert.assertNotNull(normalized.getRecordProperty(p));
			}
			count++;
		}
		Assert.assertEquals(3, sid);
		Assert.assertEquals(3, count);
		reader.close();
	}

	public static void main(String[] args) {
		if (args == null || args.length == 0)
			System.exit(-1);

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

				// System.out.println(mol.getContent());

				count++;
				if ((count % 1000) == 0) {
					now = System.currentTimeMillis();
					System.out.print(count);
					System.out.print("\t");
					System.out.println((now - start) / 100.0);
					start = now;
				}
			}
			now = System.currentTimeMillis();
			System.out.println(count);
			System.out.print(now - startRead);
			System.out.println(" msec");
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception x) {
			}
		}

	}
}
