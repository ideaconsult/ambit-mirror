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

package ambit2.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.io.setting.StringIOSetting;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.core.config.AmbitCONSTANTS;

/**
 * Reads XLS files. This implementation loads the workbook in memory which is inefficient for big files.
 * 
 * TODO find how to read it without loading into memory.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class IteratingXLSReader extends IteratingFilesWithHeaderReader<Property> {
	protected Workbook workbook;
	protected Sheet sheet;
	protected Iterator iterator;
	protected  InputStream input;
	protected int sheetIndex = 0;
	//protected HSSFFormulaEvaluator evaluator;
	protected boolean hssf = true;
	
	public IteratingXLSReader(InputStream input, int sheetIndex)  throws CDKException {
		this(input,sheetIndex,true);
	}
	public IteratingXLSReader(InputStream input, int sheetIndex,boolean hssf)  throws CDKException {
		super();
		this.hssf = hssf;
		this.sheetIndex = sheetIndex;
		setReader(input);
	}

	@Override
	protected ArrayList<Property> createHeader() {
		return new ArrayList<Property>();
	}
	@Override
	protected Property createPropertyByColumnName(String name) {
		return Property.getInstance(name,getReference());
	}
	public void setReader(InputStream input) throws CDKException {
		try {
			this.input = input;
			workbook = hssf?new HSSFWorkbook(input):new XSSFWorkbook(input);
			sheet = workbook.getSheetAt(sheetIndex);
			//evaluator = new HSSFFormulaEvaluator(sheet, workbook);
		} catch (Exception x) {
			throw new CDKException(x.getMessage(),x);
		}
		
	}
	public void setReader(Reader reader) throws CDKException {
		throw new CDKException("Not implemented");
	}
	@Override
	protected LiteratureEntry getReference() {
		return LiteratureEntry.getInstance(workbook.getSheetName(workbook.getSheetIndex(sheet)),getClass().getName());
	}
	public void processHeader() {
		iterator = sheet.rowIterator();
		//process first header line
		processHeader((Row)iterator.next());
		//skip rest of header lines
		for (int i=1; i < getNumberOfHeaderLines();i++)
			processHeader((Row)iterator.next());
	}

	public void close() throws IOException {
		input.close();
		input = null;
		iterator = null;
		sheet = null;
		workbook = null;

	}

	public boolean hasNext() {
		if (isHeaderEmpty()) {
	    	fireIOSettingQuestion(new StringIOSetting("",IOSetting.MEDIUM,Property.IO_QUESTION.IO_START.toString(),""));
			processHeader();
	    	fireIOSettingQuestion(new StringIOSetting("",IOSetting.MEDIUM,Property.IO_QUESTION.IO_STOP.toString(),""));
		}
		try {
			return iterator.hasNext();
		} catch (Exception x) {
			logger.log(Level.SEVERE,x.getMessage(),x);
			return false;
		}
	}
	protected void processRow(IAtomContainer mol) {
		
	}
	public Object next() {
		IAtomContainer mol = null;
		Map properties = new Hashtable();
		try {
			Row row = (Row) iterator.next();
			
			for (int col = 0; col < getNumberOfColumns(); col++ ) {
				Cell cell = row.getCell(col);
				Object value = null;
				if (cell != null)				
				switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BOOLEAN:
				    	value = cell.getBooleanCellValue();
				    	break;
					case Cell.CELL_TYPE_NUMERIC:
				    	value = cell.getNumericCellValue();
				    	break;
					case Cell.CELL_TYPE_STRING:
				    	value = cell.getStringCellValue();
				    	break;
					case Cell.CELL_TYPE_BLANK:
						value = "";
				    	break;
					case Cell.CELL_TYPE_ERROR:
						value = "";
				    	break;
					case Cell.CELL_TYPE_FORMULA: 
						try {
							value = cell.getStringCellValue();
					    	break;
						} catch (Exception x) {
							try {
								value = cell.getNumericCellValue();
							} catch (Exception z) {	
								logger.log(Level.WARNING,x.getMessage(),x); 
							}
						}
					}
				else 
					value = "";
				try {
					if (smilesIndex == col) {
						try {
							mol = sp.parseSmiles(value.toString());
							properties.put(AmbitCONSTANTS.SMILES, value.toString());
						} catch (InvalidSmilesException x) {
							logger.warning("Invalid SMILES!\t"+value);
							properties.put(AmbitCONSTANTS.SMILES, "Invalid SMILES");
						}						
					} 
					else 
						if (col< getNumberOfColumns())
							properties.put(getHeaderColumn(col), value);
				} catch (Exception x) {
					logger.log(Level.WARNING,x.getMessage(),x);
				}
	
			}
			if (mol == null) mol = SilentChemObjectBuilder.getInstance().newInstance(IMolecule.class);
			mol.setProperties(properties);
			processRow(mol);
		} catch (Exception x) {
			logger.log(Level.SEVERE,x.getMessage(),x);
		}
		return mol;
		
	}
	
	protected void processHeader(Row row) {
			
			Iterator cols = row.cellIterator();
			TreeMap columns = new TreeMap();
			while (cols.hasNext()) {
				Cell cell = (Cell) cols.next();
				String value = cell.getStringCellValue();

				if (value.equals(defaultSMILESHeader))
					smilesIndex = cell.getColumnIndex();
				columns.put(new Integer(cell.getColumnIndex()), value);
			}
			Iterator i = columns.keySet().iterator();
			while (i.hasNext()) {
				Integer key = (Integer)i.next();
				setHeaderColumn(key.intValue(), columns.get(key).toString());
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


