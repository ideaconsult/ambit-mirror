/*
Copyright (C) 2005-2008  

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

package ambit2.plugin.pbt;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellValue;

import ambit2.base.data.AmbitBean;

import com.jgoodies.binding.PresentationModel;

public class PBTWorksheet  extends AmbitBean  {
	protected NumberFormat format = NumberFormat.getInstance(Locale.US);
	protected PBTTableModel moreInfo= null;
	protected Object oldValue[][] ;

	protected int maxRow = 22;
	protected int maxCol = 6;
	protected int rowOffset = 0;
	protected PresentationModel<PBTWorksheet> model = new PresentationModel<PBTWorksheet>(this);

	public PresentationModel<PBTWorksheet> getModel() {
		return model;
	}
	public void setModel(PresentationModel<PBTWorksheet> model) {
		this.model = model;
	}

	protected static HSSFWorkbook workbook;
	protected HSSFSheet sheet;

	protected HSSFFormulaEvaluator formulaEvaluator = null;
	protected boolean modified = false;
	
	public boolean isModified() {
		return modified;
	}
	public void setModified(boolean modified) {
		this.modified = modified;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5725226752787199270L;
	public PBTWorksheet(HSSFWorkbook workbook, String sheetName) {
		this(workbook,sheetName,28,8,0,null);
	}
	public PBTWorksheet(HSSFWorkbook workbook, String sheetName, int maxRow, int maxCol) {
		this(workbook,sheetName,maxRow,maxCol,0,null);
	}
	public PBTWorksheet(HSSFWorkbook workbook, String sheetName, int maxRow, int maxCol, int rowOffset,String xmlConfig) {
		format.setMaximumFractionDigits(4);
		this.rowOffset = rowOffset;
		PBTWorksheet.workbook = workbook;
		sheet = workbook.getSheet(sheetName);
		if (formulaEvaluator == null)
			formulaEvaluator = new HSSFFormulaEvaluator(workbook);		
		setMaxCol(maxCol);
		setMaxRow(maxRow);
		if (xmlConfig != null) {
			moreInfo = new PBTTableModel();
			try {
				moreInfo.setDefinition(xmlConfig);
				
			} catch (Exception x) {
				moreInfo = null;
			}
		}
	
	}
	public void clear() {

        Iterator<HSSFRow> rows = getSheet().rowIterator();
        
		while (rows.hasNext()) {
			HSSFRow row = rows.next();
			
			int rowNum = row.getRowNum();
			Iterator<HSSFCell> cells = row.cellIterator();
			while (cells.hasNext()) {
				HSSFCell cell = cells.next();
				String propertyName = PBTWorksheet.getCellName(cell.getRowIndex(),cell.getColumnIndex()).toLowerCase();				
				Cell extCell = getExtendedCell(cell.getRowIndex(),cell.getColumnIndex());
				if (extCell == null) continue;
				switch (extCell.getMode()) {
				case NODE_LIST: {
					cell.setCellValue("");
					break;
				}
				case NODE_INPUT: {
					model.setValue(propertyName, null);
					break;
				}
				
				}
			}
		}
		model.notifyAll();
	}
	public CellValue evaluate(HSSFCell cell) {
		if (formulaEvaluator!=null)
			return formulaEvaluator.evaluate(cell);
		else return null;
	}
	public HSSFSheet getSheet() {
		return sheet;
	}
	public HSSFWorkbook getWorkbook() {
		return workbook;
	}		
	public int getMaxRow() {
		return maxRow;
	}

	public void setMaxRow(int maxRow) {
		this.maxRow = maxRow;
		oldValue = new Object[maxRow][maxCol];
	}

	public int getMaxCol() {
		return maxCol;
	}

	public void setMaxCol(int maxCol) {
		this.maxCol = maxCol;
		oldValue = new Object[maxRow][maxCol];
	}
	
	public Cell getExtendedCell(int row, int col) {
		if (moreInfo != null)
			return (Cell)moreInfo.getValueAt(row, col);
		else return null;
	}
	public Object getExtendedCellValue(int row, int col) {
		Cell cell = getExtendedCell(row, col);
		if (cell != null) return cell.getObject();
		else return null;
	}	
	public void setExtendedCell(Object value,int row, int col) {
		if (moreInfo != null) {
			setModified(true);
			moreInfo.setValueAt(value, row, col);
	    	firePropertyChange("E"+getCellName(row, col).toLowerCase(), null, value);
		}

	}	
	public HSSFCell getCell(int row, int col) {
		HSSFRow arow = sheet.getRow(row);
		if (arow != null) return arow.getCell(col);
		else return null;
	}
	protected Object getValue(HSSFCell cell) {
		if (cell == null) return "";
	   	switch (cell.getCellType()) {
    	case HSSFCell.CELL_TYPE_FORMULA: {
    		switch (cell.getCachedFormulaResultType()) {
        	case HSSFCell.CELL_TYPE_NUMERIC: {
        		return cell.getNumericCellValue();
        	}    	
        	case HSSFCell.CELL_TYPE_BOOLEAN: {
        		return Boolean.toString(cell.getBooleanCellValue());
        	}    
        	case HSSFCell.CELL_TYPE_STRING: {
        		return cell.getStringCellValue();
        	}    
        	case HSSFCell.CELL_TYPE_BLANK: {
        		return cell.getStringCellValue();
        	}         
        	case HSSFCell.CELL_TYPE_ERROR: {
        		return cell.getStringCellValue();
        	}                 	
    		default:
    			return cell.getStringCellValue();
    		}
    		
    	}
    	default: {
			switch (cell.getCellStyle().getDataFormat()) {
			//Text
			case 0x31 : {
			    return cell.getStringCellValue();
			}
			//General
			case 0: {
			    return cell.getStringCellValue();					
			} 
			//numeric "0.00E+00"
			case 0xb: {
				return (cell.getCellType()==HSSFCell.CELL_TYPE_BLANK)?Double.NaN:cell.getNumericCellValue();						
			}
			//numeric "0.0"
			case 0xb2: {
				return (cell.getCellType()==HSSFCell.CELL_TYPE_BLANK)?Double.NaN:cell.getNumericCellValue();								
			}
			//numeric "0.00"
			case 0x2: {
				return (cell.getCellType()==HSSFCell.CELL_TYPE_BLANK)?Double.NaN:cell.getNumericCellValue();											
			}					
			default: {
				return cell.getStringCellValue();	
			}
			}
    	}
    	}
	}
    public Object get(int row,int col) {
    	return getValue(getCell(row, col));
    }
    
    protected String format(Object value) {
    	if (value instanceof Number)
    		return format.format(value);
    	else return value.toString();
    }
    
    protected void setValue(HSSFCell cell, Object value) {
    		
    		setModified(true);
			switch (cell.getCellStyle().getDataFormat()) {
			//Text
			case 0x31 : {
				if ((value != null) && !value.equals("")) cell.setCellValue(value.toString());
				else cell.setCellType(HSSFCell.CELL_TYPE_BLANK);
				break;
			}
			//General
			case 0: {
				if ((value != null) && !value.equals("")) cell.setCellValue(value.toString());
				else cell.setCellType(HSSFCell.CELL_TYPE_BLANK);
				break;				
			} 
			//numeric "0.00E+00"
			case 0xb: {
    			try {
    				cell.setCellValue(((Number)value).doubleValue());
    			} catch (Exception x) {
    				try {
    					Double d = Double.parseDouble(value.toString());
    					if (d.equals(Double.NaN)) cell.setCellType(HSSFCell.CELL_TYPE_BLANK); else cell.setCellValue(d);
    				} catch (Exception xx) { 
    					cell.setCellType(HSSFCell.CELL_TYPE_BLANK);
    				}
    			}
				break;				
			}
			//numeric "0.0"
			case 0xb2: {
    			try {
    				cell.setCellValue(((Number)value).doubleValue());
    			} catch (Exception x) {
    				try {
    					Double d = Double.parseDouble(value.toString());
    					if (d.equals(Double.NaN)) cell.setCellType(HSSFCell.CELL_TYPE_BLANK); else cell.setCellValue(d);
    				} catch (Exception xx) {
    					cell.setCellType(HSSFCell.CELL_TYPE_BLANK); }
    			}				
				break;				
			}
			//numeric "0.00"
			case 0x2: {
    			try {
    				cell.setCellValue(((Number)value).doubleValue());
    			} catch (Exception x) {
    				try {
    					Double d = Double.parseDouble(value.toString());
    					if (d.equals(Double.NaN)) cell.setCellType(HSSFCell.CELL_TYPE_BLANK); else cell.setCellValue(d);
    				} catch (Exception xx) { 
    					cell.setCellType(HSSFCell.CELL_TYPE_BLANK);}
    			}
				break;				
			}					
			default: {
				if ((value != null) && !value.equals("")) cell.setCellValue(value.toString());
				else cell.setCellType(HSSFCell.CELL_TYPE_BLANK);
				break;				
			}
			}

    		    		
 
    }
    public void set(int row,int col, Object value) {

		HSSFCell theCell = getCell(row, col);
		if (theCell == null) return;
    	if (theCell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) return ;
    	

			for (int r=0; r < maxRow; r++)
				for (int c=0; c < maxCol; c++) {
					HSSFCell cell = getCell(row, col);
					if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
						Object o = get(r,c);
						if (o != null)
							oldValue[r][c] = o;
						else 
							oldValue[r][c] = "";
					}
				}
	    	oldValue[row][col] = get(row,col);    	
    	
    	setValue(theCell, value);
    	
   		firePropertyChange(getCellName(row, col).toLowerCase(), oldValue[row][col], get(row,col));
   		//if (notify) {

    		formulaEvaluator.notifyUpdateCell(theCell);
    		notifyCells(row, col);    		
    		//formulaEvaluator.clearAllCachedResultValues();
    		HSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);

    		notifyCells(row, col);    		
		//}
    	
	}

	public void notifyCells(int row, int col) {
//		workbook.getNumberOfSheets()
		
		for (int r=0; r < maxRow; r++)
			for (int c=0; c < maxCol; c++) 
				if ((r == row) && (c == col)) ;
				else {
					HSSFCell cell = getCell(r,c);
					
					if ((cell !=null) && (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA)) {
						firePropertyChange(getCellName(r, c).toLowerCase(), oldValue[r][c],  format(get(r,c)));
					}
				}
		
	
	}
	public static String getCellName(int row, int col) {
		if (row < 0) return null;
		if (col < 0) return null;
		StringBuilder b = new StringBuilder();
		if (col > 25)
			b.append(Character.toChars(65+col/26-1));
		b.append(Character.toChars(65+col % 26));
		b.append(Integer.toString(row+1));
		return b.toString();
	}
	

	public Object getA1() {
		return get(0,0);
	}

	public void setA1(Object value) {
		 set(0,0,value);
	}
	public Object getB1() {
		return get(0,1);
	}

	public void setB1(Object value) {
		 set(0,1,value);
	}
	public Object getC1() {
		return get(0,2);
	}

	public void setC1(Object value) {
		 set(0,2,value);
	}
	public Object getD1() {
		return get(0,3);
	}

	public void setD1(Object value) {
		 set(0,3,value);
	}
	public Object getE1() {
		return get(0,4);
	}

	public void setE1(Object value) {
		 set(0,4,value);
	}
	public Object getF1() {
		return get(0,5);
	}

	public void setF1(Object value) {
		 set(0,5,value);
	}
	public Object getG1() {
		return get(0,6);
	}

	public void setG1(Object value) {
		 set(0,6,value);
	}
	public Object getH1() {
		return get(0,7);
	}

	public void setH1(Object value) {
		 set(0,7,value);
	}
	public Object getA2() {
		return get(1,0);
	}

	public void setA2(Object value) {
		 set(1,0,value);
	}
	public Object getB2() {
		return get(1,1);
	}

	public void setB2(Object value) {
		 set(1,1,value);
	}
	public Object getC2() {
		return get(1,2);
	}

	public void setC2(Object value) {
		 set(1,2,value);
	}
	public Object getD2() {
		return get(1,3);
	}

	public void setD2(Object value) {
		 set(1,3,value);
	}
	public Object getE2() {
		return get(1,4);
	}

	public void setE2(Object value) {
		 set(1,4,value);
	}
	public Object getF2() {
		return get(1,5);
	}

	public void setF2(Object value) {
		 set(1,5,value);
	}
	public Object getG2() {
		return get(1,6);
	}

	public void setG2(Object value) {
		 set(1,6,value);
	}
	public Object getH2() {
		return get(1,7);
	}

	public void setH2(Object value) {
		 set(1,7,value);
	}
	public Object getA3() {
		return get(2,0);
	}

	public void setA3(Object value) {
		 set(2,0,value);
	}
	public Object getB3() {
		return get(2,1);
	}

	public void setB3(Object value) {
		 set(2,1,value);
	}
	public Object getC3() {
		return get(2,2);
	}

	public void setC3(Object value) {
		 set(2,2,value);
	}
	public Object getD3() {
		return get(2,3);
	}

	public void setD3(Object value) {
		 set(2,3,value);
	}
	public Object getE3() {
		return get(2,4);
	}

	public void setE3(Object value) {
		 set(2,4,value);
	}
	public Object getF3() {
		return get(2,5);
	}

	public void setF3(Object value) {
		 set(2,5,value);
	}
	public Object getG3() {
		return get(2,6);
	}

	public void setG3(Object value) {
		 set(2,6,value);
	}
	public Object getH3() {
		return get(2,7);
	}

	public void setH3(Object value) {
		 set(2,7,value);
	}
	public Object getA4() {
		return get(3,0);
	}

	public void setA4(Object value) {
		 set(3,0,value);
	}
	public Object getB4() {
		return get(3,1);
	}

	public void setB4(Object value) {
		 set(3,1,value);
	}
	public Object getC4() {
		return get(3,2);
	}

	public void setC4(Object value) {
		 set(3,2,value);
	}
	public Object getD4() {
		return get(3,3);
	}

	public void setD4(Object value) {
		 set(3,3,value);
	}
	public Object getE4() {
		return get(3,4);
	}

	public void setE4(Object value) {
		 set(3,4,value);
	}
	public Object getF4() {
		return get(3,5);
	}

	public void setF4(Object value) {
		 set(3,5,value);
	}
	public Object getG4() {
		return get(3,6);
	}

	public void setG4(Object value) {
		 set(3,6,value);
	}
	public Object getH4() {
		return get(3,7);
	}

	public void setH4(Object value) {
		 set(3,7,value);
	}
	public Object getA5() {
		return get(4,0);
	}

	public void setA5(Object value) {
		 set(4,0,value);
	}
	public Object getB5() {
		return get(4,1);
	}

	public void setB5(Object value) {
		 set(4,1,value);
	}
	public Object getC5() {
		return get(4,2);
	}

	public void setC5(Object value) {
		 set(4,2,value);
	}
	public Object getD5() {
		return get(4,3);
	}

	public void setD5(Object value) {
		 set(4,3,value);
	}
	public Object getE5() {
		return get(4,4);
	}

	public void setE5(Object value) {
		 set(4,4,value);
	}
	public Object getF5() {
		return get(4,5);
	}

	public void setF5(Object value) {
		 set(4,5,value);
	}
	public Object getG5() {
		return get(4,6);
	}

	public void setG5(Object value) {
		 set(4,6,value);
	}
	public Object getH5() {
		return get(4,7);
	}

	public void setH5(Object value) {
		 set(4,7,value);
	}
	public Object getA6() {
		return get(5,0);
	}

	public void setA6(Object value) {
		 set(5,0,value);
	}
	public Object getB6() {
		return get(5,1);
	}

	public void setB6(Object value) {
		 set(5,1,value);
	}
	public Object getC6() {
		return get(5,2);
	}

	public void setC6(Object value) {
		 set(5,2,value);
	}
	public Object getD6() {
		return get(5,3);
	}

	public void setD6(Object value) {
		 set(5,3,value);
	}
	public Object getE6() {
		return get(5,4);
	}

	public void setE6(Object value) {
		 set(5,4,value);
	}
	public Object getF6() {
		return get(5,5);
	}

	public void setF6(Object value) {
		 set(5,5,value);
	}
	public Object getG6() {
		return get(5,6);
	}

	public void setG6(Object value) {
		 set(5,6,value);
	}
	public Object getH6() {
		return get(5,7);
	}

	public void setH6(Object value) {
		 set(5,7,value);
	}
	public Object getA7() {
		return get(6,0);
	}

	public void setA7(Object value) {
		 set(6,0,value);
	}
	public Object getB7() {
		return get(6,1);
	}

	public void setB7(Object value) {
		 set(6,1,value);
	}
	public Object getC7() {
		return get(6,2);
	}

	public void setC7(Object value) {
		 set(6,2,value);
	}
	public Object getD7() {
		return get(6,3);
	}

	public void setD7(Object value) {
		 set(6,3,value);
	}
	public Object getE7() {
		return get(6,4);
	}

	public void setE7(Object value) {
		 set(6,4,value);
	}
	public Object getF7() {
		return get(6,5);
	}

	public void setF7(Object value) {
		 set(6,5,value);
	}
	public Object getG7() {
		return get(6,6);
	}

	public void setG7(Object value) {
		 set(6,6,value);
	}
	public Object getH7() {
		return get(6,7);
	}

	public void setH7(Object value) {
		 set(6,7,value);
	}
	public Object getA8() {
		return get(7,0);
	}

	public void setA8(Object value) {
		 set(7,0,value);
	}
	public Object getB8() {
		return get(7,1);
	}

	public void setB8(Object value) {
		 set(7,1,value);
	}
	public Object getC8() {
		return get(7,2);
	}

	public void setC8(Object value) {
		 set(7,2,value);
	}
	public Object getD8() {
		return get(7,3);
	}

	public void setD8(Object value) {
		 set(7,3,value);
	}
	public Object getE8() {
		return get(7,4);
	}

	public void setE8(Object value) {
		 set(7,4,value);
	}
	public Object getF8() {
		return get(7,5);
	}

	public void setF8(Object value) {
		 set(7,5,value);
	}
	public Object getG8() {
		return get(7,6);
	}

	public void setG8(Object value) {
		 set(7,6,value);
	}
	public Object getH8() {
		return get(7,7);
	}

	public void setH8(Object value) {
		 set(7,7,value);
	}
	public Object getA9() {
		return get(8,0);
	}

	public void setA9(Object value) {
		 set(8,0,value);
	}
	public Object getB9() {
		return get(8,1);
	}

	public void setB9(Object value) {
		 set(8,1,value);
	}
	public Object getC9() {
		return get(8,2);
	}

	public void setC9(Object value) {
		 set(8,2,value);
	}
	public Object getD9() {
		return get(8,3);
	}

	public void setD9(Object value) {
		 set(8,3,value);
	}
	public Object getE9() {
		return get(8,4);
	}

	public void setE9(Object value) {
		 set(8,4,value);
	}
	public Object getF9() {
		return get(8,5);
	}

	public void setF9(Object value) {
		 set(8,5,value);
	}
	public Object getG9() {
		return get(8,6);
	}

	public void setG9(Object value) {
		 set(8,6,value);
	}
	public Object getH9() {
		return get(8,7);
	}

	public void setH9(Object value) {
		 set(8,7,value);
	}
	public Object getA10() {
		return get(9,0);
	}

	public void setA10(Object value) {
		 set(9,0,value);
	}
	public Object getB10() {
		return get(9,1);
	}

	public void setB10(Object value) {
		 set(9,1,value);
	}
	public Object getC10() {
		return get(9,2);
	}

	public void setC10(Object value) {
		 set(9,2,value);
	}
	public Object getD10() {
		return get(9,3);
	}

	public void setD10(Object value) {
		 set(9,3,value);
	}
	public Object getE10() {
		return get(9,4);
	}

	public void setE10(Object value) {
		 set(9,4,value);
	}
	public Object getF10() {
		return get(9,5);
	}

	public void setF10(Object value) {
		 set(9,5,value);
	}
	public Object getG10() {
		return get(9,6);
	}

	public void setG10(Object value) {
		 set(9,6,value);
	}
	public Object getH10() {
		return get(9,7);
	}

	public void setH10(Object value) {
		 set(9,7,value);
	}
	public Object getA11() {
		return get(10,0);
	}

	public void setA11(Object value) {
		 set(10,0,value);
	}
	public Object getB11() {
		return get(10,1);
	}

	public void setB11(Object value) {
		 set(10,1,value);
	}
	public Object getC11() {
		return get(10,2);
	}

	public void setC11(Object value) {
		 set(10,2,value);
	}
	public Object getD11() {
		return get(10,3);
	}

	public void setD11(Object value) {
		 set(10,3,value);
	}
	public Object getE11() {
		return get(10,4);
	}

	public void setE11(Object value) {
		 set(10,4,value);
	}
	public Object getF11() {
		return get(10,5);
	}

	public void setF11(Object value) {
		 set(10,5,value);
	}
	public Object getG11() {
		return get(10,6);
	}

	public void setG11(Object value) {
		 set(10,6,value);
	}
	public Object getH11() {
		return get(10,7);
	}

	public void setH11(Object value) {
		 set(10,7,value);
	}
	public Object getA12() {
		return get(11,0);
	}

	public void setA12(Object value) {
		 set(11,0,value);
	}
	public Object getB12() {
		return get(11,1);
	}

	public void setB12(Object value) {
		 set(11,1,value);
	}
	public Object getC12() {
		return get(11,2);
	}

	public void setC12(Object value) {
		 set(11,2,value);
	}
	public Object getD12() {
		return get(11,3);
	}

	public void setD12(Object value) {
		 set(11,3,value);
	}
	public Object getE12() {
		return get(11,4);
	}

	public void setE12(Object value) {
		 set(11,4,value);
	}
	public Object getF12() {
		return get(11,5);
	}

	public void setF12(Object value) {
		 set(11,5,value);
	}
	public Object getG12() {
		return get(11,6);
	}

	public void setG12(Object value) {
		 set(11,6,value);
	}
	public Object getH12() {
		return get(11,7);
	}

	public void setH12(Object value) {
		 set(11,7,value);
	}
	public Object getA13() {
		return get(12,0);
	}

	public void setA13(Object value) {
		 set(12,0,value);
	}
	public Object getB13() {
		return get(12,1);
	}

	public void setB13(Object value) {
		 set(12,1,value);
	}
	public Object getC13() {
		return get(12,2);
	}

	public void setC13(Object value) {
		 set(12,2,value);
	}
	public Object getD13() {
		return get(12,3);
	}

	public void setD13(Object value) {
		 set(12,3,value);
	}
	public Object getE13() {
		return get(12,4);
	}

	public void setE13(Object value) {
		 set(12,4,value);
	}
	public Object getF13() {
		return get(12,5);
	}

	public void setF13(Object value) {
		 set(12,5,value);
	}
	public Object getG13() {
		return get(12,6);
	}

	public void setG13(Object value) {
		 set(12,6,value);
	}
	public Object getH13() {
		return get(12,7);
	}

	public void setH13(Object value) {
		 set(12,7,value);
	}
	public Object getA14() {
		return get(13,0);
	}

	public void setA14(Object value) {
		 set(13,0,value);
	}
	public Object getB14() {
		return get(13,1);
	}

	public void setB14(Object value) {
		 set(13,1,value);
	}
	public Object getC14() {
		return get(13,2);
	}

	public void setC14(Object value) {
		 set(13,2,value);
	}
	public Object getD14() {
		return get(13,3);
	}

	public void setD14(Object value) {
		 set(13,3,value);
	}
	public Object getE14() {
		return get(13,4);
	}

	public void setE14(Object value) {
		 set(13,4,value);
	}
	public Object getF14() {
		return get(13,5);
	}

	public void setF14(Object value) {
		 set(13,5,value);
	}
	public Object getG14() {
		return get(13,6);
	}

	public void setG14(Object value) {
		 set(13,6,value);
	}
	public Object getH14() {
		return get(13,7);
	}

	public void setH14(Object value) {
		 set(13,7,value);
	}
	public Object getA15() {
		return get(14,0);
	}

	public void setA15(Object value) {
		 set(14,0,value);
	}
	public Object getB15() {
		return get(14,1);
	}

	public void setB15(Object value) {
		 set(14,1,value);
	}
	public Object getC15() {
		return get(14,2);
	}
	public Object getB93() {
		return get(92,1);
	}
	public Object getB87() {
		return get(86,1);
	}
	public Object getB86() {
		return get(85,1);
	}

	public void setC15(Object value) {
		 set(14,2,value);
	}
	public Object getD15() {
		return get(14,3);
	}

	public void setD15(Object value) {
		 set(14,3,value);
	}
	public Object getE15() {
		return get(14,4);
	}

	public void setE15(Object value) {
		 set(14,4,value);
	}
	public Object getF15() {
		return get(14,5);
	}

	public void setF15(Object value) {
		 set(14,5,value);
	}
	public Object getG15() {
		return get(14,6);
	}

	public void setG15(Object value) {
		 set(14,6,value);
	}
	public Object getH15() {
		return get(14,7);
	}

	public void setH15(Object value) {
		 set(14,7,value);
	}
	public Object getA16() {
		return get(15,0);
	}

	public void setA16(Object value) {
		 set(15,0,value);
	}
	public Object getB16() {
		return get(15,1);
	}

	public void setB16(Object value) {
		 set(15,1,value);
	}
	public Object getC16() {
		return get(15,2);
	}

	public void setC16(Object value) {
		 set(15,2,value);
	}
	public Object getD16() {
		return get(15,3);
	}

	public void setD16(Object value) {
		 set(15,3,value);
	}
	public Object getE16() {
		return get(15,4);
	}

	public void setE16(Object value) {
		 set(15,4,value);
	}
	public Object getF16() {
		return get(15,5);
	}

	public void setF16(Object value) {
		 set(15,5,value);
	}
	public Object getG16() {
		return get(15,6);
	}

	public void setG16(Object value) {
		 set(15,6,value);
	}
	public Object getH16() {
		return get(15,7);
	}

	public void setH16(Object value) {
		 set(15,7,value);
	}
	public Object getA17() {
		return get(16,0);
	}

	public void setA17(Object value) {
		 set(16,0,value);
	}
	public Object getB17() {
		return get(16,1);
	}

	public void setB17(Object value) {
		 set(16,1,value);
	}
	public Object getC17() {
		return get(16,2);
	}

	public void setC17(Object value) {
		 set(16,2,value);
	}
	public Object getD17() {
		return get(16,3);
	}

	public void setD17(Object value) {
		 set(16,3,value);
	}
	public Object getE17() {
		return get(16,4);
	}

	public void setE17(Object value) {
		 set(16,4,value);
	}
	public Object getF17() {
		return get(16,5);
	}

	public void setF17(Object value) {
		 set(16,5,value);
	}
	public Object getG17() {
		return get(16,6);
	}

	public void setG17(Object value) {
		 set(16,6,value);
	}
	public Object getH17() {
		return get(16,7);
	}

	public void setH17(Object value) {
		 set(16,7,value);
	}
	public Object getA18() {
		return get(17,0);
	}

	public void setA18(Object value) {
		 set(17,0,value);
	}
	public Object getB18() {
		return get(17,1);
	}

	public void setB18(Object value) {
		 set(17,1,value);
	}
	public Object getC18() {
		return get(17,2);
	}

	public void setC18(Object value) {
		 set(17,2,value);
	}
	public Object getD18() {
		return get(17,3);
	}

	public void setD18(Object value) {
		 set(17,3,value);
	}
	public Object getE18() {
		return get(17,4);
	}

	public void setE18(Object value) {
		 set(17,4,value);
	}
	public Object getF18() {
		return get(17,5);
	}

	public void setF18(Object value) {
		 set(17,5,value);
	}
	public Object getG18() {
		return get(17,6);
	}

	public void setG18(Object value) {
		 set(17,6,value);
	}
	public Object getH18() {
		return get(17,7);
	}

	public void setH18(Object value) {
		 set(17,7,value);
	}
	public Object getA19() {
		return get(18,0);
	}

	public void setA19(Object value) {
		 set(18,0,value);
	}
	public Object getB19() {
		return get(18,1);
	}

	public void setB19(Object value) {
		 set(18,1,value);
	}
	public Object getC19() {
		return get(18,2);
	}

	public void setC19(Object value) {
		 set(18,2,value);
	}
	public Object getD19() {
		return get(18,3);
	}

	public void setD19(Object value) {
		 set(18,3,value);
	}
	public Object getE19() {
		return get(18,4);
	}

	public void setE19(Object value) {
		 set(18,4,value);
	}
	public Object getF19() {
		return get(18,5);
	}

	public void setF19(Object value) {
		 set(18,5,value);
	}
	public Object getG19() {
		return get(18,6);
	}

	public void setG19(Object value) {
		 set(18,6,value);
	}
	public Object getH19() {
		return get(18,7);
	}

	public void setH19(Object value) {
		 set(18,7,value);
	}
	public Object getA20() {
		return get(19,0);
	}

	public void setA20(Object value) {
		 set(19,0,value);
	}
	public Object getB20() {
		return get(19,1);
	}

	public void setB20(Object value) {
		 set(19,1,value);
	}
	public Object getC20() {
		return get(19,2);
	}

	public void setC20(Object value) {
		 set(19,2,value);
	}
	public Object getD20() {
		return get(19,3);
	}

	public void setD20(Object value) {
		 set(19,3,value);
	}
	public Object getE20() {
		return get(19,4);
	}

	public void setE20(Object value) {
		 set(19,4,value);
	}
	public Object getF20() {
		return get(19,5);
	}

	public void setF20(Object value) {
		 set(19,5,value);
	}
	public Object getG20() {
		return get(19,6);
	}

	public void setG20(Object value) {
		 set(19,6,value);
	}
	public Object getH20() {
		return get(19,7);
	}

	public void setH20(Object value) {
		 set(19,7,value);
	}
	public Object getA21() {
		return get(20,0);
	}

	public void setA21(Object value) {
		 set(20,0,value);
	}
	public Object getB21() {
		return get(20,1);
	}

	public void setB21(Object value) {
		 set(20,1,value);
	}
	public Object getC21() {
		return get(20,2);
	}

	public void setC21(Object value) {
		 set(20,2,value);
	}
	public Object getD21() {
		return get(20,3);
	}

	public void setD21(Object value) {
		 set(20,3,value);
	}
	public Object getE21() {
		return get(20,4);
	}

	public void setE21(Object value) {
		 set(20,4,value);
	}
	public Object getF21() {
		return get(20,5);
	}

	public void setF21(Object value) {
		 set(20,5,value);
	}
	public Object getG21() {
		return get(20,6);
	}

	public void setG21(Object value) {
		 set(20,6,value);
	}
	public Object getH21() {
		return get(20,7);
	}

	public void setH21(Object value) {
		 set(20,7,value);
	}
	public Object getA22() {
		return get(21,0);
	}

	public void setA22(Object value) {
		 set(21,0,value);
	}
	public Object getB22() {
		return get(21,1);
	}

	public void setB22(Object value) {
		 set(21,1,value);
	}
	public Object getC22() {
		return get(21,2);
	}

	public void setC22(Object value) {
		 set(21,2,value);
	}
	public Object getD22() {
		return get(21,3);
	}

	public void setD22(Object value) {
		 set(21,3,value);
	}
	public Object getE22() {
		return get(21,4);
	}

	public void setE22(Object value) {
		 set(21,4,value);
	}
	public Object getF22() {
		return get(21,5);
	}

	public void setF22(Object value) {
		 set(21,5,value);
	}
	public Object getG22() {
		return get(21,6);
	}

	public void setG22(Object value) {
		 set(21,6,value);
	}
	public Object getH22() {
		return get(21,7);
	}

	public void setH22(Object value) {
		 set(21,7,value);
	}
	public Object getA23() {
		return get(22,0);
	}

	public void setA23(Object value) {
		 set(22,0,value);
	}
	public Object getB23() {
		return get(22,1);
	}

	public void setB23(Object value) {
		 set(22,1,value);
	}
	public Object getC23() {
		return get(22,2);
	}

	public void setC23(Object value) {
		 set(22,2,value);
	}
	public Object getD23() {
		return get(22,3);
	}

	public void setD23(Object value) {
		 set(22,3,value);
	}
	public Object getE23() {
		return get(22,4);
	}

	public void setE23(Object value) {
		 set(22,4,value);
	}
	public Object getF23() {
		return get(22,5);
	}

	public void setF23(Object value) {
		 set(22,5,value);
	}
	public Object getG23() {
		return get(22,6);
	}

	public void setG23(Object value) {
		 set(22,6,value);
	}
	public Object getH23() {
		return get(22,7);
	}

	public void setH23(Object value) {
		 set(22,7,value);
	}
	public Object getA24() {
		return get(23,0);
	}

	public void setA24(Object value) {
		 set(23,0,value);
	}
	public Object getB24() {
		return get(23,1);
	}

	public void setB24(Object value) {
		 set(23,1,value);
	}
	public Object getC24() {
		return get(23,2);
	}

	public void setC24(Object value) {
		 set(23,2,value);
	}
	public Object getD24() {
		return get(23,3);
	}

	public void setD24(Object value) {
		 set(23,3,value);
	}
	public Object getE24() {
		return get(23,4);
	}

	public void setE24(Object value) {
		 set(23,4,value);
	}
	public Object getF24() {
		return get(23,5);
	}

	public void setF24(Object value) {
		 set(23,5,value);
	}
	public Object getG24() {
		return get(23,6);
	}

	public void setG24(Object value) {
		 set(23,6,value);
	}
	public Object getH24() {
		return get(23,7);
	}

	public void setH24(Object value) {
		 set(23,7,value);
	}
	public Object getA25() {
		return get(24,0);
	}

	public void setA25(Object value) {
		 set(24,0,value);
	}
	public Object getB25() {
		return get(24,1);
	}

	public void setB25(Object value) {
		 set(24,1,value);
	}
	public Object getC25() {
		return get(24,2);
	}

	public void setC25(Object value) {
		 set(24,2,value);
	}
	public Object getD25() {
		return get(24,3);
	}

	public void setD25(Object value) {
		 set(24,3,value);
	}
	public Object getE25() {
		return get(24,4);
	}

	public void setE25(Object value) {
		 set(24,4,value);
	}
	public Object getF25() {
		return get(24,5);
	}

	public void setF25(Object value) {
		 set(24,5,value);
	}
	public Object getG25() {
		return get(24,6);
	}

	public void setG25(Object value) {
		 set(24,6,value);
	}
	public Object getH25() {
		return get(24,7);
	}

	public void setH25(Object value) {
		 set(24,7,value);
	}
	public Object getA26() {
		return get(25,0);
	}

	public void setA26(Object value) {
		 set(25,0,value);
	}
	public Object getB26() {
		return get(25,1);
	}

	public void setB26(Object value) {
		 set(25,1,value);
	}
	public Object getC26() {
		return get(25,2);
	}

	public void setC26(Object value) {
		 set(25,2,value);
	}
	public Object getD26() {
		return get(25,3);
	}

	public void setD26(Object value) {
		 set(25,3,value);
	}
	public Object getE26() {
		return get(25,4);
	}

	public void setE26(Object value) {
		 set(25,4,value);
	}
	public Object getF26() {
		return get(25,5);
	}

	public void setF26(Object value) {
		 set(25,5,value);
	}
	public Object getG26() {
		return get(25,6);
	}

	public void setG26(Object value) {
		 set(25,6,value);
	}
	public Object getH26() {
		return get(25,7);
	}

	public void setH26(Object value) {
		 set(25,7,value);
	}
	public Object getA27() {
		return get(26,0);
	}

	public void setA27(Object value) {
		 set(26,0,value);
	}
	public Object getB27() {
		return get(26,1);
	}

	public void setB27(Object value) {
		 set(26,1,value);
	}
	public Object getC27() {
		return get(26,2);
	}

	public void setC27(Object value) {
		 set(26,2,value);
	}
	public Object getD27() {
		return get(26,3);
	}

	public void setD27(Object value) {
		 set(26,3,value);
	}
	public Object getE27() {
		return get(26,4);
	}

	public void setE27(Object value) {
		 set(26,4,value);
	}
	public Object getF27() {
		return get(26,5);
	}

	public void setF27(Object value) {
		 set(26,5,value);
	}
	public Object getG27() {
		return get(26,6);
	}

	public void setG27(Object value) {
		 set(26,6,value);
	}
	public Object getH27() {
		return get(26,7);
	}

	public void setH27(Object value) {
		 set(26,7,value);
	}
	public Object getA28() {
		return get(27,0);
	}

	public void setA28(Object value) {
		 set(27,0,value);
	}
	public Object getB28() {
		return get(27,1);
	}

	public void setB28(Object value) {
		 set(27,1,value);
	}
	public Object getC28() {
		return get(27,2);
	}

	public void setC28(Object value) {
		 set(27,2,value);
	}
	public Object getD28() {
		return get(27,3);
	}

	public void setD28(Object value) {
		 set(27,3,value);
	}
	public Object getE28() {
		return get(27,4);
	}

	public void setE28(Object value) {
		 set(27,4,value);
	}
	public Object getF28() {
		return get(27,5);
	}

	public void setF28(Object value) {
		 set(27,5,value);
	}
	public Object getG28() {
		return get(27,6);
	}

	public void setG28(Object value) {
		 set(27,6,value);
	}
	public Object getH28() {
		return get(27,7);
	}

	public void setH28(Object value) {
		 set(27,7,value);
	}
}







