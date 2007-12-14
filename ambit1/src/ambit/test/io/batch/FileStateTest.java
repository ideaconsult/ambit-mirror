/*
 * Created on 2005-9-4
 *
 */
package ambit.test.io.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.exceptions.AmbitIOException;
import ambit.io.FileInputState;
import ambit.io.FileOutputState;
import ambit.io.FileState;

/**
 * TODO add description
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-9-4
 */
public class FileStateTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(FileStateTest.class);
	}
	public void testMatch() {
		File f = new File("data/test.cml");
		assertTrue(f.exists());
		
		
		FileState fs = new FileState(f);
		try {
			assertTrue(fs.match());
			File f1 = new File("data/test.cml");
			assertTrue(fs.match(f1));
		} catch (AmbitIOException x) {
			x.printStackTrace();
			fail();
		}
	}
	public void testRoundTrip() {
		FileState fs = new FileState();
		fs.setFilename("test.cml");
		fs.setLastModified(999);
		fs.setLength(777);
		fs.setHashCode(888);
		fs.setCurrentRecord(10);
		
		FileState newFS = new FileState();
		assertNotSame(fs,newFS);
		
		try {
			//writing
			File fwrite = File.createTempFile("FileStateTest","test");
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(fwrite));
			os.writeObject(fs);
			os.close();
			
			String filename = fwrite.getAbsolutePath();
			System.out.println(filename);
			
			//reading
			File fread = new File(filename);
			ObjectInputStream is = new ObjectInputStream(new FileInputStream(fread));
			newFS =(FileState) is.readObject();
			is.close();
			fread.delete();
			assertEquals(fs,newFS);
			
			//f.delete();
		} catch (IOException x) {
			x.printStackTrace();
			fail();
		} catch (ClassNotFoundException x) {
			x.printStackTrace();
			fail();			
		}
		
		
		
	}
	public void testFileInputState() {
		FileInputState fs = new FileInputState("data/misc/Debnath.csv");
		try {
			IIteratingChemObjectReader reader = fs.getReader();
			assertNotNull(reader);
			int count = 0;
			while (reader.hasNext()) {
				reader.next();
				count++;
			}
			assertEquals(88,count);
			reader.close();
		} catch (AmbitIOException x) {
			x.printStackTrace();
			fail();			
		} catch (IOException x) {
			x.printStackTrace();
			fail();
		}		
	}
	public void testFileOutputState() {
		FileInputState fs = new FileInputState("data/misc/Debnath_smiles.csv");
		FileOutputState fo = new FileOutputState("data/misc/Debnath_test.sdf");
		try {
			IChemObjectWriter writer = fo.getWriter();
			IIteratingChemObjectReader reader = fs.getReader();
			assertNotNull(reader);
			int read_count = 0;
			int write_count = 0;
			while (reader.hasNext()) {
				Object o = reader.next();
				read_count++;
				if (o instanceof IChemObject) {
					writer.write((IChemObject)o);
					write_count++;
				}	
			}
			assertEquals(88,read_count);
			assertEquals(88,write_count);
			reader.close();
			writer.close();
		} catch (AmbitIOException x) {
			x.printStackTrace();
			fail();
		} catch (IOException x) {
			x.printStackTrace();
			fail();
		} catch (CDKException x) {
			x.printStackTrace();
			fail();
		}			
	}	
}
