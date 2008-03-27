/*
 Copyright (C) 2005-2006  

 Contact: nina@acad.bg

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation; either version 2.1
 of the License, or (at your option) any later version.
 All we ask is that proper credit is given for our work, which includes
 - but is not limited to - adding the above copyright notice to the beginning
 of your source code files, and to any copyright notice that you may distribute
 with programs based on this work.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
 */

package ambit2.test.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.io.FileInputState;



public class POItest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(POItest.class);
	}

	/**
	 * Read an excel file and spit out what we find.
	 * 
	 * @param file
	 *            Expect one argument that is the file to read.
	 * @throws IOException
	 *             When there is an error processing the file.
	 */
	public void readXLSFile(String file) throws IOException {
		// create a new file input stream with the input file specified
		// at the command line
		FileInputStream fin = new FileInputStream(file);
		// create a new org.apache.poi.poifs.filesystem.Filesystem
		POIFSFileSystem poifs = new POIFSFileSystem(fin);
		// get the Workbook (excel part) stream in a InputStream
		InputStream din = poifs.createDocumentInputStream("Workbook");
		// construct out HSSFRequest object
		HSSFRequest req = new HSSFRequest();
		// lazy listen for ALL records with the listener shown above
		req.addListenerForAllRecords(new POIExample());
		// create our event factory
		HSSFEventFactory factory = new HSSFEventFactory();
		// process our events based on the document input stream
		factory.processEvents(req, din);
		// once all the events are processed close our file input stream
		fin.close();
		// and our document input stream (don't want to leak these!)
		din.close();
		System.out.println("done.");
	}

	public void test() {
		try {
			readXLSFile("data/misc/Debnath_smiles.xls");
			
			assertTrue(true);
		} catch (IOException x) {
			x.printStackTrace();
			fail();
		}
	}
	public void test1() {
		try {
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream("data/misc/Debnath_smiles.xls"));
			HSSFSheet sheet = workbook.getSheetAt(0);
			//HSSFSheet sheet = workbook.getSheet("Sheet1");
			
			Iterator i = sheet.rowIterator();
			while (i.hasNext()) {
				Object o = i.next();
				assertTrue(o instanceof HSSFRow);
				Iterator j = ((HSSFRow) o).cellIterator();
				while (j.hasNext()) {
					Object cell = j.next();
					assertTrue(cell instanceof HSSFCell);
					System.out.println(cell);
	
				}
				
			}

			
			//HSSFCell cell = row.getCell((short)0);
		} catch (IOException x) {
			x.printStackTrace();
			fail();	
		}
		
	}
	public void testIteratingXLSReader() {
		readXLS("data/misc/Debnath_smiles.xls",88);		
	}
	
	public void testFormulaReader() {
		readXLS("data/misc/BCF-example.xls",36);		
	}	

	public void readXLS(String file, int rows) {
		try {
			IIteratingChemObjectReader reader = FileInputState.getReader(
					new FileInputStream(file), file);
			int r = 0;
			while (reader.hasNext()) {
				Object mol = reader.next();
				assertTrue(mol instanceof IMolecule);
				//assertTrue(((Molecule) mol).getAtomCount() > 0);
				System.out.println(((IMolecule)mol).getProperties());
				r++;
			}
			assertEquals(rows,r);
		} catch (Exception x) {
			x.printStackTrace();
			fail();	
		}
	}
}
