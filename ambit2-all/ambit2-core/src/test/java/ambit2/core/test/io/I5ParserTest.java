package ambit2.core.test.io;

import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.I5ReaderSimple;

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
			/*
			for (Property p : record.getProperties())
				System.out.println(String.format("%s [%s] = %s",p.getName(),p.getReference().getTitle(),record.getProperty(p)));
			System.out.println();
			*/
			count++;
			for (Property p :record.getProperties()) {
				foundCas += record.getProperty(p).equals("59-87-0")?1:0;
				foundName += record.getProperty(p).equals("5-nitro-2-furaldehyde semicarbazone")?1:0;
				System.out.println(p.getName() + " = " + record.getProperty(p));
			}
			Assert.assertNotNull(record.getSmiles());
			Assert.assertNotNull(record.getInchi());
			Assert.assertNotNull(record.getProperty(Property.getI5UUIDInstance()));
		}
		reader.close();
		Assert.assertEquals(1,count);
		Assert.assertEquals(1,foundCas);
		Assert.assertEquals(1,foundName);
	}	
}
