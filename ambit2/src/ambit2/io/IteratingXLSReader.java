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

package ambit2.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.setting.IOSetting;

import ambit2.config.AmbitCONSTANTS;
import ambit2.exceptions.AmbitIOException;

/**
 * Reads XLS files. This implementation loads the workbook in memory which is inefficient for big files.
 * 
 * TODO find how to read it without loading into memory.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class IteratingXLSReader extends IteratingFilesWithHeaderReader {
	protected HSSFWorkbook workbook;
	protected HSSFSheet sheet;
	protected Iterator iterator;
	protected  InputStream input;
	protected IOSetting[] headerOptions = null;
	//protected HSSFFormulaEvaluator evaluator;
	
	public IteratingXLSReader(InputStream input, int sheetIndex)  throws AmbitIOException {
		super();
		try {
			this.input = input;
			workbook = new HSSFWorkbook(input);
			sheet = workbook.getSheetAt(sheetIndex);
			iterator = sheet.rowIterator();
			//evaluator = new HSSFFormulaEvaluator(sheet, workbook);

			//process first header line
			processHeader((HSSFRow)iterator.next());
			//skip rest of header lines
			for (int i=1; i < getNumberOfHeaderLines();i++)
				processHeader((HSSFRow)iterator.next());
		} catch (Exception x) {
			throw new AmbitIOException(x);
		}
	}

	public void close() throws IOException {
		input.close();
		input = null;
		iterator = null;
		sheet = null;
		workbook = null;

	}

	public boolean hasNext() {
		try {
		    if (headerOptions == null) {
		        headerOptions = setHeaderOptions(header);
		        for (int i=0; i < headerOptions.length;i++)
		            fireIOSettingQuestion(headerOptions[i]);
		    }
			return iterator.hasNext();
		} catch (Exception x) {
			logger.error(x);
			return false;
		}
	}

	public Object next() {
		IMolecule mol = null;
		try {
			HSSFRow row = (HSSFRow) iterator.next();
			Iterator cols = row.cellIterator();
			Hashtable properties = new Hashtable();
	
			while (cols.hasNext()) {
				HSSFCell cell = (HSSFCell) cols.next();
				
				Object value = cell.toString();
				if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
					/*
					try {
					HSSFFormulaEvaluator.CellValue cellValue = evaluator.evaluate(cell);
					switch (cellValue.getCellType()) {
					case HSSFCell.CELL_TYPE_BOOLEAN:
				    	value = cellValue.getBooleanValue();
				    	break;
					case HSSFCell.CELL_TYPE_NUMERIC:
				    	value = cellValue.getNumberValue();
				    	break;
					case HSSFCell.CELL_TYPE_STRING:
				    	value = cellValue.toString();
				    	break;
					case HSSFCell.CELL_TYPE_BLANK:
						value = "";
				    	break;
					case HSSFCell.CELL_TYPE_ERROR:
						value = "";
				    	break;

					// CELL_TYPE_FORMULA will never happen
					case HSSFCell.CELL_TYPE_FORMULA: 
				    	break;
					}
					} catch (Exception x) {
						x.printStackTrace();
						value = "";
					}
					*/
				}	
				
				if (smilesIndex == cell.getCellNum()) {
					try {
						mol = sp.parseSmiles(value.toString());
						properties.put(AmbitCONSTANTS.SMILES, value.toString());
					} catch (InvalidSmilesException x) {
						logger.warn("Invalid SMILES!\t"+value);
						properties.put(AmbitCONSTANTS.SMILES, "Invalid SMILES");
					}						
				} else properties.put(header.get(cell.getCellNum()).toString(), value);
	
			}
			if (mol == null) mol = new Molecule();
			mol.setProperties(properties);
		} catch (Exception x) {
			logger.error(x);
		}
		return mol;
		
	}
	
	protected void processHeader(HSSFRow row) {
			
			Iterator cols = row.cellIterator();
			TreeMap columns = new TreeMap();
			while (cols.hasNext()) {
				HSSFCell cell = (HSSFCell) cols.next();
				String value = cell.getStringCellValue();
				/*
				System.out.print(cell.getCellNum());
				System.out.print("\t");
				System.out.println(value);
				*/
				if (value.equals(defaultSMILESHeader))
					smilesIndex = cell.getCellNum();
				columns.put(new Integer(cell.getCellNum()), value);
			}
			Iterator i = columns.keySet().iterator();
			while (i.hasNext()) {
				Integer key = (Integer)i.next();
				header.ensureCapacity(key);
				while (key.intValue() >= header.size())
					header.add("");
				header.set(key,columns.get(key));
			}
	}

	public String toString() {
		return "Reads Microsoft Office Excel file (*.xls) " ;
	}
	/* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#getFormat()
     */
    public IResourceFormat getFormat() {
        return new XLSFileFormat();
    }
}


