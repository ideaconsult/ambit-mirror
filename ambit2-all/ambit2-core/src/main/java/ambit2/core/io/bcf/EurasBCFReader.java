package ambit2.core.io.bcf;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitIOException;
import ambit2.core.io.IteratingXLSReader;

public class EurasBCFReader extends IteratingXLSReader {
	protected Hashtable<Double, LiteratureEntry> references;
	public EurasBCFReader(InputStream input, int sheetIndex)
			throws AmbitIOException {
		super(input, sheetIndex);
		setNumberOfHeaderLines(2);
		references = new Hashtable<Double, LiteratureEntry>();
	}
	protected void readReferences() {
		HSSFSheet sheet = workbook.getSheetAt(1);
		Iterator<HSSFRow> i = sheet.rowIterator();
		while (i.hasNext()) {
			HSSFRow row = i.next();
			if (row.getRowNum()==0) continue;
			//Object index = row.getCell(0).getStringCellValue();
			Double index = row.getCell(0).getNumericCellValue();
			LiteratureEntry ref = getCitation(row);
			if (ref != null)
			references.put(index,ref);
		}
	}
	protected LiteratureEntry getCitation(HSSFRow row) {
		return LiteratureEntry.getInstance(
		String.format("%s,\"%s\" %s %d-%s (%d): %s." , 
				getAuthor(row),
				getTitle(row),
				getJournal(row),
				getVolume(row),
				getIssue(row),
				getYear(row),
				getPages(row)
				),
				getJournal(row));
		
	}
	protected String getAuthor(HSSFRow row) {
		return row.getCell(1).getStringCellValue();
	}
	protected String getTitle(HSSFRow row) {
		return row.getCell(2).getStringCellValue();
	}	
	protected String getJournal(HSSFRow row) {
		return row.getCell(3).getStringCellValue();
	}	
	protected int getYear(HSSFRow row) {
		Double d = row.getCell(4).getNumericCellValue();
		return d.intValue();
	}	
	protected int getVolume(HSSFRow row) {
		Double d = row.getCell(5).getNumericCellValue();
		return d.intValue();
	}	
	protected String getIssue(HSSFRow row) {
		if (row.getCell(6)==null) return "";
		else 
			return Integer.toString(((Double)row.getCell(6).getNumericCellValue()).intValue());

	}		
	protected String getPages(HSSFRow row) {
		try {
			return row.getCell(7).getStringCellValue();
		} catch (Exception x) {
			return Integer.toString(((Double)row.getCell(7).getNumericCellValue()).intValue());
		}
	}	
	@Override
	public void processHeader() {
		iterator = sheet.rowIterator();
		//process first header line
		processHeader((HSSFRow)iterator.next());
		//skip rest of header lines
		HSSFRow row = sheet.getRow(1);
		for (int i=0; i < getNumberOfColumns(); i++) {
			HSSFCell cell = row.getCell(i);
			if (cell == null) continue;
			getHeaderColumn(i).setUnits(cell.getStringCellValue());
		}
		
		readReferences();
	}
	@Override
	protected void processRow(IAtomContainer mol) {
		Double index = (Double)mol.getProperty(Property.getInstance("ref",getReference()));
		mol.setProperty(Property.getInstance("REFERENCE", getClass().getName()), references.get(index));
	}

}
