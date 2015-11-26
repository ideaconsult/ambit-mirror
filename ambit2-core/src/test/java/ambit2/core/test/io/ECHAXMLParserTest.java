package ambit2.core.test.io;

import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.ECHAPreregistrationListReader;

/**
<pre>
REGISTRATION_DATE [ECHA] = 30.11.2010
CasRN [ECHA] = 147-14-8
EC [ECHA] = 205-685-1
Names [ECHA SYNONYM#3] = Pigment Blue 15:3
Names [ECHA SYNONYM#2] = Pigment Blue 15:4
Names [ECHA SYNONYM#1] = Pigment Blue 15
Names [ECHA] = 29H,31H-phthalocyaninato(2-)-N29,N30,N31,N32 copper
</pre>
 * @author nina
 *
 */
public class ECHAXMLParserTest {
	@Test
	public void test() throws Exception {
		InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/core/data/echa_preregistration.echaxml");
		ECHAPreregistrationListReader reader = new ECHAPreregistrationListReader(in);
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
				foundCas += record.getRecordProperty(p).equals("8008-57-9")?1:0;
				foundName += record.getRecordProperty(p).equals("4'-methoxyacetanilide")?1:0;
			}
		}
		reader.close();
		Assert.assertEquals(12,count);
		Assert.assertEquals(1,foundCas);
		Assert.assertEquals(1,foundName);
		//0008008-57-9
	}
}
