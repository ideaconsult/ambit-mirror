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

package ambit2.core.test.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.listener.IReaderListener;
import org.openscience.cdk.io.setting.IOSetting;

import ambit2.core.io.FileInputState;
import ambit2.core.io.SimpleIOListener;



public class POItest {

	/**
	 * Read an excel file and spit out what we find.
	 * 
	 * @param file
	 *            Expect one argument that is the file to read.
	 * @throws IOException
	 *             When there is an error processing the file.
	 */
	public void readXLSFile(String file) throws Exception {
		
		// create a new file input stream with the input file specified
		// at the command line
		FileInputStream fin = new FileInputStream(getClass().getClassLoader().getResource(file).getFile());
		// create a new org.apache.poi.poifs.filesystem.Filesystem
		POIFSFileSystem poifs = new POIFSFileSystem(fin);
		// get the Workbook (excel part) stream in a InputStream
		InputStream din = poifs.createDocumentInputStream("Workbook");
		// construct out HSSFRequest object
		HSSFRequest req = new HSSFRequest();
		// lazy listen for ALL records with the listener shown above
		req.addListenerForAllRecords(new POIListener());
		// create our event factory
		HSSFEventFactory factory = new HSSFEventFactory();
		// process our events based on the document input stream
		factory.processEvents(req, din);
		// once all the events are processed close our file input stream
		fin.close();
		// and our document input stream (don't want to leak these!)
		din.close();
	}

	@Test public void test() throws Exception  {
			readXLSFile("ambit2/core/data/misc/Debnath_smiles.xls");
			
			Assert.assertTrue(true);
	}
	@Test public void test1() throws Exception {
			InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/core/data/misc/Debnath_smiles.xls");
			Workbook workbook = new HSSFWorkbook(in);
			Sheet sheet = workbook.getSheetAt(0);
			//HSSFSheet sheet = workbook.getSheet("Sheet1");
			
			Iterator i = sheet.rowIterator();
			while (i.hasNext()) {
				Object o = i.next();
				Assert.assertTrue(o instanceof Row);
				Iterator j = ((Row) o).cellIterator();
				while (j.hasNext()) {
					Object cell = j.next();
					Assert.assertTrue(cell instanceof Cell);
					//System.out.println(cell);
	
				}
			}
	}
	@Test public void testIteratingXLSReader() throws Exception {
		readXLS("ambit2/core/data/misc/Debnath_smiles.xls",88);		
	}
	@Test public void testIteratingXLSXReader() throws Exception {
		readXLS("ambit2/core/data/misc/Debnath_smiles.xlsx",88);		
	}
	/* The BCF-example.xls file had been lost
	@Test public void testFormulaReader() throws Exception {
		//there are many empty rows with formulae
		readXLS("ambit2/core/data/misc/BCF-example.xls",1853);		
	}	
	*/
	public void readXLS(String file, int rows) throws Exception {
		
			String filepath = getClass().getClassLoader().getResource(file).getFile();
			IIteratingChemObjectReader reader = FileInputState.getReader(
					new FileInputStream(filepath), filepath);
			IReaderListener listener = new SimpleIOListener(IOSetting.HIGH);
			reader.addChemObjectIOListener(listener);
			int r = 0;
			while (reader.hasNext()) {
				Object mol = reader.next();
				Assert.assertTrue(mol instanceof IMolecule);
				//assertTrue(((Molecule) mol).getAtomCount() > 0);
				//System.out.println(((IMolecule)mol).getProperties());
				r++;
				//System.out.println(r);
			}
			Assert.assertEquals(rows,r);

	}
}

class POIListener implements HSSFListener {
	private SSTRecord sstrec;

	public POIListener() {
		super();
	}


	public void processRecord(Record record)
    {
        switch (record.getSid())
        {
            // the BOFRecord can represent either the beginning of a sheet or the workbook
            case BOFRecord.sid:
                BOFRecord bof = (BOFRecord) record;
                if (bof.getType() == bof.TYPE_WORKBOOK)
                {
                    //System.out.println("Encountered workbook");
                    // assigned to the class level member
                } else if (bof.getType() == bof.TYPE_WORKSHEET)
                {
                    //System.out.println("Encountered sheet reference");
                }
                break;
            case BoundSheetRecord.sid:
                BoundSheetRecord bsr = (BoundSheetRecord) record;
               // System.out.println("New sheet named: " + bsr.getSheetname());
                break;
            case RowRecord.sid:
                RowRecord rowrec = (RowRecord) record;
                //System.out.println("Row found, first column at "
                //        + rowrec.getFirstCol() + " last column at " + rowrec.getLastCol());
                break;
            case NumberRecord.sid:
                NumberRecord numrec = (NumberRecord) record;
               // System.out.println("Cell found with value " + numrec.getValue()
             //           + " at row " + numrec.getRow() + " and column " + numrec.getColumn());
                break;
                // SSTRecords store a array of unique strings used in Excel.
            case SSTRecord.sid:
                sstrec = (SSTRecord) record;
                for (int k = 0; k < sstrec.getNumUniqueStrings(); k++)
                {
                    //System.out.println("String table value " + k + " = " + sstrec.getString(k));
                }
                break;
            case LabelSSTRecord.sid:
                LabelSSTRecord lrec = (LabelSSTRecord) record;
               // System.out.println("String cell found with value "
                //        + sstrec.getString(lrec.getSSTIndex()));
                break;
        }
    }

}
