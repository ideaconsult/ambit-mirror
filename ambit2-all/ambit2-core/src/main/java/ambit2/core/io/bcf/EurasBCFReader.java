package ambit2.core.io.bcf;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.core.io.IteratingXLSReader;

public class EurasBCFReader extends IteratingXLSReader {
	protected Hashtable<Double, LiteratureEntry> references;
	public EurasBCFReader(InputStream input, int sheetIndex)
			throws CDKException {
		super(input, sheetIndex);
		setNumberOfHeaderLines(2);
		references = new Hashtable<Double, LiteratureEntry>();
	}
	protected void readReferences() {
		Sheet sheet = workbook.getSheetAt(1);
		Iterator<Row> i = sheet.rowIterator();
		while (i.hasNext()) {
			Row row = i.next();
			if (row.getRowNum()==0) continue;
			//Object index = row.getCell(0).getStringCellValue();
			Double index = row.getCell(0).getNumericCellValue();
			LiteratureEntry ref = getCitation(row);
			if (ref != null)
			references.put(index,ref);
		}
	}
	protected LiteratureEntry getCitation(Row row) {
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
	protected String getAuthor(Row row) {
		return row.getCell(1).getStringCellValue();
	}
	protected String getTitle(Row row) {
		return row.getCell(2).getStringCellValue();
	}	
	protected String getJournal(Row row) {
		return row.getCell(3).getStringCellValue();
	}	
	protected int getYear(Row row) {
		Double d = row.getCell(4).getNumericCellValue();
		return d.intValue();
	}	
	protected int getVolume(Row row) {
		Double d = row.getCell(5).getNumericCellValue();
		return d.intValue();
	}	
	protected String getIssue(Row row) {
		if (row.getCell(6)==null) return "";
		else 
			return Integer.toString(((Double)row.getCell(6).getNumericCellValue()).intValue());

	}		
	protected String getPages(Row row) {
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
		processHeader((Row)iterator.next());
		//skip rest of header lines
		Row row = (Row)iterator.next();
		for (int i=0; i < getNumberOfColumns(); i++) {
			Cell cell = row.getCell(i);
			if (cell == null) continue;
			getHeaderColumn(i).setUnits(cell.getStringCellValue());
		}
		
		readReferences();
	}
	@Override
	protected void processRow(IAtomContainer mol) {
		Object o = mol.getProperty(Property.getInstance("ref",getReference()));
		try {
			Double index = (Double) o;
			mol.setProperty("REFERENCE", references.get(index));
		} catch (Exception x) {
			mol.setProperty("REFERENCE", LiteratureEntry.getInstance(o.toString(),o.toString()));
		}
		
	}

}
