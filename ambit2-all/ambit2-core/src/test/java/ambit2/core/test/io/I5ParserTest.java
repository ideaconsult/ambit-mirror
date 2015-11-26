package ambit2.core.test.io;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.io.IChemObjectReaderErrorHandler;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.FileInputState;
import ambit2.core.io.I5ReaderSimple;
import ambit2.core.io.RawIteratingFolderReader;

public class I5ParserTest {
	@Test
	public void test() throws Exception {
		InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/core/data/i5d/ECB5-2c94e32c-3662-4dea-ba00-43787b8a6fd3_0.i5d");
		I5ReaderSimple reader = new I5ReaderSimple(in);
		int count = 0;
		int foundCas=0;
		int foundName=0;
		while (reader.hasNext()) {
			IStructureRecord record = reader.nextRecord();

			count++;
			for (Property p :record.getRecordProperties()) {
				foundCas += record.getRecordProperty(p).equals("59-87-0")?1:0;
				foundName += record.getRecordProperty(p).equals("5-nitro-2-furaldehyde semicarbazone")?1:0;
				System.out.println(p.getName() + " = " + record.getRecordProperty(p));
			}
			Assert.assertNotNull(record.getSmiles());
			Assert.assertNotNull(record.getContent());
			Assert.assertNotNull(record.getRecordProperty(Property.getI5UUIDInstance()));
		}
		reader.close();
		Assert.assertEquals(1,count);
		Assert.assertEquals(1,foundCas);
		Assert.assertEquals(1,foundName);
	}	
	
	
	@Test
	public void testi5z() throws Exception {
		URL url = getClass().getClassLoader().getResource("ambit2/core/data/i5z/RefSub_030913110311.i5z");
		IIteratingChemObjectReader ireader = FileInputState.getReader(new File(url.getFile()));
		ireader.setErrorHandler(new IChemObjectReaderErrorHandler() {
			@Override
			public void handleError(String message, int row, int colStart, int colEnd,
					Exception exception) {
			}
			
			@Override
			public void handleError(String message, int row, int colStart, int colEnd) {
			}
			
			@Override
			public void handleError(String message, Exception exception) {
				exception.printStackTrace();
			}
			
			@Override
			public void handleError(String message) {
			}
		});
		int count = 0;
		int foundCas=0;
		int foundName=0;
		RawIteratingFolderReader reader = (RawIteratingFolderReader) ireader;
		while (reader.hasNext()) {
			IStructureRecord record = reader.nextRecord();
			/*
			for (Property p : record.getProperties())
				System.out.println(String.format("%s [%s] = %s",p.getName(),p.getReference().getTitle(),record.getProperty(p)));
			System.out.println();
			*/
			count++;
			for (Property p :record.getRecordProperties()) {
				foundCas += record.getRecordProperty(p).equals("59-87-0")?1:0;
				foundName += record.getRecordProperty(p).equals("5-nitro-2-furaldehyde semicarbazone")?1:0;
				System.out.println(p.getName() + " = " + record.getRecordProperty(p));
			}
			//Assert.assertNotNull(record.getSmiles());
			//Assert.assertNotNull(record.getInchi());
			//Assert.assertNotNull(record.getProperty(Property.getI5UUIDInstance()));
		}
		reader.close();
		Assert.assertEquals(10,count);
		Assert.assertEquals(1,foundCas);
		Assert.assertEquals(1,foundName);
	}		
	
	@Test
	public void testi5dFolder() throws Exception {
		URL url  = getClass().getClassLoader().getResource("ambit2/core/data/i5z/RefSub_030913110311");
		
		File dir = new File(url.getFile());
		
		File[] files = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith("i5d");
			}
		});
		Assert.assertEquals(20,files.length);
		RawIteratingFolderReader reader = new RawIteratingFolderReader(files);
		int count = 0;
		int foundInChI=0;
		int foundCas=0;
		int foundName=0;
		while (reader.hasNext()) {
			IStructureRecord record = reader.nextRecord();

			count++;
			if (record.getContent()!=null && "INC".equals(record.getFormat())) {
				foundInChI++;
				System.out.println(record.getContent());
			}
			for (Property p :record.getRecordProperties()) {
				foundCas += record.getRecordProperty(p).equals("59-87-0")?1:0;
				foundName += record.getRecordProperty(p).equals("5-nitro-2-furaldehyde semicarbazone")?1:0;
				System.out.println(p.getName() + " = " + record.getRecordProperty(p));
			}
			//Assert.assertNotNull(record.getSmiles());
			//Assert.assertNotNull(record.getInchi());
			//Assert.assertNotNull(record.getProperty(Property.getI5UUIDInstance()));
		}
		reader.close();
		Assert.assertEquals(10,count);
		Assert.assertEquals(10,foundInChI);
	}	
	
	
	@Test
	public void testSubstanceComposition() throws Exception {
		InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/core/data/i5d/TestSubstance3.i5d");

		I5ReaderSimple reader = new I5ReaderSimple(new InputStreamReader(in,"UTF-8"));
		int count = 0;
		int foundCas=0;
		int foundName=0;
		while (reader.hasNext()) {
			IStructureRecord record = reader.nextRecord();
			/*
			for (Property p : record.getProperties())
				System.out.println(String.format("%s [%s] = %s",p.getName(),p.getReference().getTitle(),record.getProperty(p)));
			System.out.println();
			*/
			count++;
			for (Property p :record.getRecordProperties()) {
				foundCas += record.getRecordProperty(p).equals("59-87-0")?1:0;
				foundName += record.getRecordProperty(p).equals("5-nitro-2-furaldehyde semicarbazone")?1:0;
				System.out.println(p.getName() + " = " + record.getRecordProperty(p));
			}
			//Assert.assertNotNull(record.getSmiles());
			Assert.assertNotNull(record.getContent());
			Assert.assertNotNull(record.getRecordProperty(Property.getI5UUIDInstance()));
		}
		reader.close();
		Assert.assertEquals(1,count);
		Assert.assertEquals(1,foundCas);
		Assert.assertEquals(1,foundName);
	}
}
