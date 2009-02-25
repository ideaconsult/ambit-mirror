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

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellValue;

import ambit2.core.data.AmbitBean;

public class PBTWorksheet  extends AmbitBean  {
	protected PBTTableModel moreInfo= null;
	protected String oldValue[][] ;

	protected int maxRow = 22;
	protected int maxCol = 6;
	


	protected static HSSFWorkbook workbook;
	protected HSSFSheet sheet;

	protected HSSFFormulaEvaluator formulaEvaluator = null;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5725226752787199270L;
	public PBTWorksheet(HSSFWorkbook workbook, String sheetName) {
		this(workbook,sheetName,28,8,null);
	}
	public PBTWorksheet(HSSFWorkbook workbook, String sheetName, int maxRow, int maxCol) {
		this(workbook,sheetName,maxRow,maxCol,null);
	}
	public PBTWorksheet(HSSFWorkbook workbook, String sheetName, int maxRow, int maxCol, String xmlConfig) {
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
		oldValue = new String[maxRow][maxCol];
	}

	public int getMaxCol() {
		return maxCol;
	}

	public void setMaxCol(int maxCol) {
		this.maxCol = maxCol;
		oldValue = new String[maxRow][maxCol];
	}
	
	public Object getExtendedCell(int row, int col) {
		if (moreInfo != null)
			return moreInfo.getValueAt(row, col);
		else return null;
	}
	public void setExtendedCell(Object value,int row, int col) {
		if (moreInfo != null) {
			moreInfo.setValueAt(value, row, col);
	    	firePropertyChange("E"+getCellName(row-1, col-1).toLowerCase(), null, value);
		}

	}	
	public HSSFCell getCell(int row, int col) {
		HSSFRow arow = sheet.getRow(row);
		if (arow != null) return arow.getCell(col);
		else return null;
	}
	
    public Object get(int row,int col) {
    	HSSFCell cell = getCell(row, col);
    	if (cell == null) return "";
    	
    	switch (cell.getCellType()) {
    	case HSSFCell.CELL_TYPE_FORMULA: {
    		switch (cell.getCachedFormulaResultType()) {
        	case HSSFCell.CELL_TYPE_NUMERIC: {
        		return cell.getNumericCellValue();
        	}    		
    		default:
    			return cell.getStringCellValue();
    		}
    		
    	}
    	case HSSFCell.CELL_TYPE_NUMERIC: {
    		return cell.getNumericCellValue();
    	}
    	case HSSFCell.CELL_TYPE_STRING: {
    		return cell.getStringCellValue();
    	}
    	case HSSFCell.CELL_TYPE_BLANK: {
    		return cell.getStringCellValue();
    	}    	    	
    	default: {
    		return cell.getStringCellValue();
    	}
    	}
    }
	public void set(int row,int col, Object value) {
		for (int r=0; r < maxRow; r++)
			for (int c=0; c < maxCol; c++) {
				HSSFCell cell = getCell(row, col);
				if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
					Object o = get(r,c);
					if (o != null)
						oldValue[r][c] = o.toString();
					else 
						oldValue[r][c] = "";
				}
			}
		
    	HSSFCell cell = getCell(row, col);
    	oldValue[row][col] = get(row,col).toString();    	
    	
    	switch (cell.getCellType()) {
    	case HSSFCell.CELL_TYPE_FORMULA: {
    		return;
    	}
    	/*
    	case HSSFCell.CELL_TYPE_NUMERIC: {
    		cell.setCellValue(Double.parseDouble(value.toString()));
    		formulaEvaluator.notifyUpdateCell(cell);
    		HSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);    		
    	}
    	*/
    	default: {
    		if (value instanceof Number)
    			cell.setCellValue(((Number)value).doubleValue());
    		else
    			try {
    				cell.setCellValue(Double.parseDouble(value.toString()));
    			} catch (Exception x) {
    				cell.setCellValue(value.toString());
    			}
    		formulaEvaluator.notifyUpdateCell(cell);
    		//formulaEvaluator.clearAllCachedResultValues();

    		HSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
    		    		
    	}
    	}
    	firePropertyChange(getCellName(row, col).toLowerCase(), oldValue[row][col], get(row,col).toString());
    	notifyCells(row, col);

    	
	}

	public void notifyCells(int row, int col) {
//		workbook.getNumberOfSheets()
		
		for (int r=0; r < maxRow; r++)
			for (int c=0; c < maxCol; c++) 
				if ((r == row) && (c == col)) ;
				else {
					HSSFCell cell = getCell(r,c);
					
					if ((cell !=null) && (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA))
						firePropertyChange(getCellName(r, c).toLowerCase(), oldValue[r][c],  get(r,c).toString());
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
	

	public String getA1() {
		return get(0,0).toString();
	}

	public void setA1(String value) {
		 set(0,0,value);
	}
	public String getB1() {
		return get(0,1).toString();
	}

	public void setB1(String value) {
		 set(0,1,value);
	}
	public String getC1() {
		return get(0,2).toString();
	}

	public void setC1(String value) {
		 set(0,2,value);
	}
	public String getD1() {
		return get(0,3).toString();
	}

	public void setD1(String value) {
		 set(0,3,value);
	}
	public String getE1() {
		return get(0,4).toString();
	}

	public void setE1(String value) {
		 set(0,4,value);
	}
	public String getF1() {
		return get(0,5).toString();
	}

	public void setF1(String value) {
		 set(0,5,value);
	}
	public String getG1() {
		return get(0,6).toString();
	}

	public void setG1(String value) {
		 set(0,6,value);
	}
	public String getH1() {
		return get(0,7).toString();
	}

	public void setH1(String value) {
		 set(0,7,value);
	}
	public String getA2() {
		return get(1,0).toString();
	}

	public void setA2(String value) {
		 set(1,0,value);
	}
	public String getB2() {
		return get(1,1).toString();
	}

	public void setB2(String value) {
		 set(1,1,value);
	}
	public String getC2() {
		return get(1,2).toString();
	}

	public void setC2(String value) {
		 set(1,2,value);
	}
	public String getD2() {
		return get(1,3).toString();
	}

	public void setD2(String value) {
		 set(1,3,value);
	}
	public String getE2() {
		return get(1,4).toString();
	}

	public void setE2(String value) {
		 set(1,4,value);
	}
	public String getF2() {
		return get(1,5).toString();
	}

	public void setF2(String value) {
		 set(1,5,value);
	}
	public String getG2() {
		return get(1,6).toString();
	}

	public void setG2(String value) {
		 set(1,6,value);
	}
	public String getH2() {
		return get(1,7).toString();
	}

	public void setH2(String value) {
		 set(1,7,value);
	}
	public String getA3() {
		return get(2,0).toString();
	}

	public void setA3(String value) {
		 set(2,0,value);
	}
	public String getB3() {
		return get(2,1).toString();
	}

	public void setB3(String value) {
		 set(2,1,value);
	}
	public String getC3() {
		return get(2,2).toString();
	}

	public void setC3(String value) {
		 set(2,2,value);
	}
	public String getD3() {
		return get(2,3).toString();
	}

	public void setD3(String value) {
		 set(2,3,value);
	}
	public String getE3() {
		return get(2,4).toString();
	}

	public void setE3(String value) {
		 set(2,4,value);
	}
	public String getF3() {
		return get(2,5).toString();
	}

	public void setF3(String value) {
		 set(2,5,value);
	}
	public String getG3() {
		return get(2,6).toString();
	}

	public void setG3(String value) {
		 set(2,6,value);
	}
	public String getH3() {
		return get(2,7).toString();
	}

	public void setH3(String value) {
		 set(2,7,value);
	}
	public String getA4() {
		return get(3,0).toString();
	}

	public void setA4(String value) {
		 set(3,0,value);
	}
	public String getB4() {
		return get(3,1).toString();
	}

	public void setB4(String value) {
		 set(3,1,value);
	}
	public String getC4() {
		return get(3,2).toString();
	}

	public void setC4(String value) {
		 set(3,2,value);
	}
	public String getD4() {
		return get(3,3).toString();
	}

	public void setD4(String value) {
		 set(3,3,value);
	}
	public String getE4() {
		return get(3,4).toString();
	}

	public void setE4(String value) {
		 set(3,4,value);
	}
	public String getF4() {
		return get(3,5).toString();
	}

	public void setF4(String value) {
		 set(3,5,value);
	}
	public String getG4() {
		return get(3,6).toString();
	}

	public void setG4(String value) {
		 set(3,6,value);
	}
	public String getH4() {
		return get(3,7).toString();
	}

	public void setH4(String value) {
		 set(3,7,value);
	}
	public String getA5() {
		return get(4,0).toString();
	}

	public void setA5(String value) {
		 set(4,0,value);
	}
	public String getB5() {
		return get(4,1).toString();
	}

	public void setB5(String value) {
		 set(4,1,value);
	}
	public String getC5() {
		return get(4,2).toString();
	}

	public void setC5(String value) {
		 set(4,2,value);
	}
	public String getD5() {
		return get(4,3).toString();
	}

	public void setD5(String value) {
		 set(4,3,value);
	}
	public String getE5() {
		return get(4,4).toString();
	}

	public void setE5(String value) {
		 set(4,4,value);
	}
	public String getF5() {
		return get(4,5).toString();
	}

	public void setF5(String value) {
		 set(4,5,value);
	}
	public String getG5() {
		return get(4,6).toString();
	}

	public void setG5(String value) {
		 set(4,6,value);
	}
	public String getH5() {
		return get(4,7).toString();
	}

	public void setH5(String value) {
		 set(4,7,value);
	}
	public String getA6() {
		return get(5,0).toString();
	}

	public void setA6(String value) {
		 set(5,0,value);
	}
	public String getB6() {
		return get(5,1).toString();
	}

	public void setB6(String value) {
		 set(5,1,value);
	}
	public String getC6() {
		return get(5,2).toString();
	}

	public void setC6(String value) {
		 set(5,2,value);
	}
	public String getD6() {
		return get(5,3).toString();
	}

	public void setD6(String value) {
		 set(5,3,value);
	}
	public String getE6() {
		return get(5,4).toString();
	}

	public void setE6(String value) {
		 set(5,4,value);
	}
	public String getF6() {
		return get(5,5).toString();
	}

	public void setF6(String value) {
		 set(5,5,value);
	}
	public String getG6() {
		return get(5,6).toString();
	}

	public void setG6(String value) {
		 set(5,6,value);
	}
	public String getH6() {
		return get(5,7).toString();
	}

	public void setH6(String value) {
		 set(5,7,value);
	}
	public String getA7() {
		return get(6,0).toString();
	}

	public void setA7(String value) {
		 set(6,0,value);
	}
	public String getB7() {
		return get(6,1).toString();
	}

	public void setB7(String value) {
		 set(6,1,value);
	}
	public String getC7() {
		return get(6,2).toString();
	}

	public void setC7(String value) {
		 set(6,2,value);
	}
	public String getD7() {
		return get(6,3).toString();
	}

	public void setD7(String value) {
		 set(6,3,value);
	}
	public String getE7() {
		return get(6,4).toString();
	}

	public void setE7(String value) {
		 set(6,4,value);
	}
	public String getF7() {
		return get(6,5).toString();
	}

	public void setF7(String value) {
		 set(6,5,value);
	}
	public String getG7() {
		return get(6,6).toString();
	}

	public void setG7(String value) {
		 set(6,6,value);
	}
	public String getH7() {
		return get(6,7).toString();
	}

	public void setH7(String value) {
		 set(6,7,value);
	}
	public String getA8() {
		return get(7,0).toString();
	}

	public void setA8(String value) {
		 set(7,0,value);
	}
	public String getB8() {
		return get(7,1).toString();
	}

	public void setB8(String value) {
		 set(7,1,value);
	}
	public String getC8() {
		return get(7,2).toString();
	}

	public void setC8(String value) {
		 set(7,2,value);
	}
	public String getD8() {
		return get(7,3).toString();
	}

	public void setD8(String value) {
		 set(7,3,value);
	}
	public String getE8() {
		return get(7,4).toString();
	}

	public void setE8(String value) {
		 set(7,4,value);
	}
	public String getF8() {
		return get(7,5).toString();
	}

	public void setF8(String value) {
		 set(7,5,value);
	}
	public String getG8() {
		return get(7,6).toString();
	}

	public void setG8(String value) {
		 set(7,6,value);
	}
	public String getH8() {
		return get(7,7).toString();
	}

	public void setH8(String value) {
		 set(7,7,value);
	}
	public String getA9() {
		return get(8,0).toString();
	}

	public void setA9(String value) {
		 set(8,0,value);
	}
	public String getB9() {
		return get(8,1).toString();
	}

	public void setB9(String value) {
		 set(8,1,value);
	}
	public String getC9() {
		return get(8,2).toString();
	}

	public void setC9(String value) {
		 set(8,2,value);
	}
	public String getD9() {
		return get(8,3).toString();
	}

	public void setD9(String value) {
		 set(8,3,value);
	}
	public String getE9() {
		return get(8,4).toString();
	}

	public void setE9(String value) {
		 set(8,4,value);
	}
	public String getF9() {
		return get(8,5).toString();
	}

	public void setF9(String value) {
		 set(8,5,value);
	}
	public String getG9() {
		return get(8,6).toString();
	}

	public void setG9(String value) {
		 set(8,6,value);
	}
	public String getH9() {
		return get(8,7).toString();
	}

	public void setH9(String value) {
		 set(8,7,value);
	}
	public String getA10() {
		return get(9,0).toString();
	}

	public void setA10(String value) {
		 set(9,0,value);
	}
	public String getB10() {
		return get(9,1).toString();
	}

	public void setB10(String value) {
		 set(9,1,value);
	}
	public String getC10() {
		return get(9,2).toString();
	}

	public void setC10(String value) {
		 set(9,2,value);
	}
	public String getD10() {
		return get(9,3).toString();
	}

	public void setD10(String value) {
		 set(9,3,value);
	}
	public String getE10() {
		return get(9,4).toString();
	}

	public void setE10(String value) {
		 set(9,4,value);
	}
	public String getF10() {
		return get(9,5).toString();
	}

	public void setF10(String value) {
		 set(9,5,value);
	}
	public String getG10() {
		return get(9,6).toString();
	}

	public void setG10(String value) {
		 set(9,6,value);
	}
	public String getH10() {
		return get(9,7).toString();
	}

	public void setH10(String value) {
		 set(9,7,value);
	}
	public String getA11() {
		return get(10,0).toString();
	}

	public void setA11(String value) {
		 set(10,0,value);
	}
	public String getB11() {
		return get(10,1).toString();
	}

	public void setB11(String value) {
		 set(10,1,value);
	}
	public String getC11() {
		return get(10,2).toString();
	}

	public void setC11(String value) {
		 set(10,2,value);
	}
	public String getD11() {
		return get(10,3).toString();
	}

	public void setD11(String value) {
		 set(10,3,value);
	}
	public String getE11() {
		return get(10,4).toString();
	}

	public void setE11(String value) {
		 set(10,4,value);
	}
	public String getF11() {
		return get(10,5).toString();
	}

	public void setF11(String value) {
		 set(10,5,value);
	}
	public String getG11() {
		return get(10,6).toString();
	}

	public void setG11(String value) {
		 set(10,6,value);
	}
	public String getH11() {
		return get(10,7).toString();
	}

	public void setH11(String value) {
		 set(10,7,value);
	}
	public String getA12() {
		return get(11,0).toString();
	}

	public void setA12(String value) {
		 set(11,0,value);
	}
	public String getB12() {
		return get(11,1).toString();
	}

	public void setB12(String value) {
		 set(11,1,value);
	}
	public String getC12() {
		return get(11,2).toString();
	}

	public void setC12(String value) {
		 set(11,2,value);
	}
	public String getD12() {
		return get(11,3).toString();
	}

	public void setD12(String value) {
		 set(11,3,value);
	}
	public String getE12() {
		return get(11,4).toString();
	}

	public void setE12(String value) {
		 set(11,4,value);
	}
	public String getF12() {
		return get(11,5).toString();
	}

	public void setF12(String value) {
		 set(11,5,value);
	}
	public String getG12() {
		return get(11,6).toString();
	}

	public void setG12(String value) {
		 set(11,6,value);
	}
	public String getH12() {
		return get(11,7).toString();
	}

	public void setH12(String value) {
		 set(11,7,value);
	}
	public String getA13() {
		return get(12,0).toString();
	}

	public void setA13(String value) {
		 set(12,0,value);
	}
	public String getB13() {
		return get(12,1).toString();
	}

	public void setB13(String value) {
		 set(12,1,value);
	}
	public String getC13() {
		return get(12,2).toString();
	}

	public void setC13(String value) {
		 set(12,2,value);
	}
	public String getD13() {
		return get(12,3).toString();
	}

	public void setD13(String value) {
		 set(12,3,value);
	}
	public String getE13() {
		return get(12,4).toString();
	}

	public void setE13(String value) {
		 set(12,4,value);
	}
	public String getF13() {
		return get(12,5).toString();
	}

	public void setF13(String value) {
		 set(12,5,value);
	}
	public String getG13() {
		return get(12,6).toString();
	}

	public void setG13(String value) {
		 set(12,6,value);
	}
	public String getH13() {
		return get(12,7).toString();
	}

	public void setH13(String value) {
		 set(12,7,value);
	}
	public String getA14() {
		return get(13,0).toString();
	}

	public void setA14(String value) {
		 set(13,0,value);
	}
	public String getB14() {
		return get(13,1).toString();
	}

	public void setB14(String value) {
		 set(13,1,value);
	}
	public String getC14() {
		return get(13,2).toString();
	}

	public void setC14(String value) {
		 set(13,2,value);
	}
	public String getD14() {
		return get(13,3).toString();
	}

	public void setD14(String value) {
		 set(13,3,value);
	}
	public String getE14() {
		return get(13,4).toString();
	}

	public void setE14(String value) {
		 set(13,4,value);
	}
	public String getF14() {
		return get(13,5).toString();
	}

	public void setF14(String value) {
		 set(13,5,value);
	}
	public String getG14() {
		return get(13,6).toString();
	}

	public void setG14(String value) {
		 set(13,6,value);
	}
	public String getH14() {
		return get(13,7).toString();
	}

	public void setH14(String value) {
		 set(13,7,value);
	}
	public String getA15() {
		return get(14,0).toString();
	}

	public void setA15(String value) {
		 set(14,0,value);
	}
	public String getB15() {
		return get(14,1).toString();
	}

	public void setB15(String value) {
		 set(14,1,value);
	}
	public String getC15() {
		return get(14,2).toString();
	}

	public void setC15(String value) {
		 set(14,2,value);
	}
	public String getD15() {
		return get(14,3).toString();
	}

	public void setD15(String value) {
		 set(14,3,value);
	}
	public String getE15() {
		return get(14,4).toString();
	}

	public void setE15(String value) {
		 set(14,4,value);
	}
	public String getF15() {
		return get(14,5).toString();
	}

	public void setF15(String value) {
		 set(14,5,value);
	}
	public String getG15() {
		return get(14,6).toString();
	}

	public void setG15(String value) {
		 set(14,6,value);
	}
	public String getH15() {
		return get(14,7).toString();
	}

	public void setH15(String value) {
		 set(14,7,value);
	}
	public String getA16() {
		return get(15,0).toString();
	}

	public void setA16(String value) {
		 set(15,0,value);
	}
	public String getB16() {
		return get(15,1).toString();
	}

	public void setB16(String value) {
		 set(15,1,value);
	}
	public String getC16() {
		return get(15,2).toString();
	}

	public void setC16(String value) {
		 set(15,2,value);
	}
	public String getD16() {
		return get(15,3).toString();
	}

	public void setD16(String value) {
		 set(15,3,value);
	}
	public String getE16() {
		return get(15,4).toString();
	}

	public void setE16(String value) {
		 set(15,4,value);
	}
	public String getF16() {
		return get(15,5).toString();
	}

	public void setF16(String value) {
		 set(15,5,value);
	}
	public String getG16() {
		return get(15,6).toString();
	}

	public void setG16(String value) {
		 set(15,6,value);
	}
	public String getH16() {
		return get(15,7).toString();
	}

	public void setH16(String value) {
		 set(15,7,value);
	}
	public String getA17() {
		return get(16,0).toString();
	}

	public void setA17(String value) {
		 set(16,0,value);
	}
	public String getB17() {
		return get(16,1).toString();
	}

	public void setB17(String value) {
		 set(16,1,value);
	}
	public String getC17() {
		return get(16,2).toString();
	}

	public void setC17(String value) {
		 set(16,2,value);
	}
	public String getD17() {
		return get(16,3).toString();
	}

	public void setD17(String value) {
		 set(16,3,value);
	}
	public String getE17() {
		return get(16,4).toString();
	}

	public void setE17(String value) {
		 set(16,4,value);
	}
	public String getF17() {
		return get(16,5).toString();
	}

	public void setF17(String value) {
		 set(16,5,value);
	}
	public String getG17() {
		return get(16,6).toString();
	}

	public void setG17(String value) {
		 set(16,6,value);
	}
	public String getH17() {
		return get(16,7).toString();
	}

	public void setH17(String value) {
		 set(16,7,value);
	}
	public String getA18() {
		return get(17,0).toString();
	}

	public void setA18(String value) {
		 set(17,0,value);
	}
	public String getB18() {
		return get(17,1).toString();
	}

	public void setB18(String value) {
		 set(17,1,value);
	}
	public String getC18() {
		return get(17,2).toString();
	}

	public void setC18(String value) {
		 set(17,2,value);
	}
	public String getD18() {
		return get(17,3).toString();
	}

	public void setD18(String value) {
		 set(17,3,value);
	}
	public String getE18() {
		return get(17,4).toString();
	}

	public void setE18(String value) {
		 set(17,4,value);
	}
	public String getF18() {
		return get(17,5).toString();
	}

	public void setF18(String value) {
		 set(17,5,value);
	}
	public String getG18() {
		return get(17,6).toString();
	}

	public void setG18(String value) {
		 set(17,6,value);
	}
	public String getH18() {
		return get(17,7).toString();
	}

	public void setH18(String value) {
		 set(17,7,value);
	}
	public String getA19() {
		return get(18,0).toString();
	}

	public void setA19(String value) {
		 set(18,0,value);
	}
	public String getB19() {
		return get(18,1).toString();
	}

	public void setB19(String value) {
		 set(18,1,value);
	}
	public String getC19() {
		return get(18,2).toString();
	}

	public void setC19(String value) {
		 set(18,2,value);
	}
	public String getD19() {
		return get(18,3).toString();
	}

	public void setD19(String value) {
		 set(18,3,value);
	}
	public String getE19() {
		return get(18,4).toString();
	}

	public void setE19(String value) {
		 set(18,4,value);
	}
	public String getF19() {
		return get(18,5).toString();
	}

	public void setF19(String value) {
		 set(18,5,value);
	}
	public String getG19() {
		return get(18,6).toString();
	}

	public void setG19(String value) {
		 set(18,6,value);
	}
	public String getH19() {
		return get(18,7).toString();
	}

	public void setH19(String value) {
		 set(18,7,value);
	}
	public String getA20() {
		return get(19,0).toString();
	}

	public void setA20(String value) {
		 set(19,0,value);
	}
	public String getB20() {
		return get(19,1).toString();
	}

	public void setB20(String value) {
		 set(19,1,value);
	}
	public String getC20() {
		return get(19,2).toString();
	}

	public void setC20(String value) {
		 set(19,2,value);
	}
	public String getD20() {
		return get(19,3).toString();
	}

	public void setD20(String value) {
		 set(19,3,value);
	}
	public String getE20() {
		return get(19,4).toString();
	}

	public void setE20(String value) {
		 set(19,4,value);
	}
	public String getF20() {
		return get(19,5).toString();
	}

	public void setF20(String value) {
		 set(19,5,value);
	}
	public String getG20() {
		return get(19,6).toString();
	}

	public void setG20(String value) {
		 set(19,6,value);
	}
	public String getH20() {
		return get(19,7).toString();
	}

	public void setH20(String value) {
		 set(19,7,value);
	}
	public String getA21() {
		return get(20,0).toString();
	}

	public void setA21(String value) {
		 set(20,0,value);
	}
	public String getB21() {
		return get(20,1).toString();
	}

	public void setB21(String value) {
		 set(20,1,value);
	}
	public String getC21() {
		return get(20,2).toString();
	}

	public void setC21(String value) {
		 set(20,2,value);
	}
	public String getD21() {
		return get(20,3).toString();
	}

	public void setD21(String value) {
		 set(20,3,value);
	}
	public String getE21() {
		return get(20,4).toString();
	}

	public void setE21(String value) {
		 set(20,4,value);
	}
	public String getF21() {
		return get(20,5).toString();
	}

	public void setF21(String value) {
		 set(20,5,value);
	}
	public String getG21() {
		return get(20,6).toString();
	}

	public void setG21(String value) {
		 set(20,6,value);
	}
	public String getH21() {
		return get(20,7).toString();
	}

	public void setH21(String value) {
		 set(20,7,value);
	}
	public String getA22() {
		return get(21,0).toString();
	}

	public void setA22(String value) {
		 set(21,0,value);
	}
	public String getB22() {
		return get(21,1).toString();
	}

	public void setB22(String value) {
		 set(21,1,value);
	}
	public String getC22() {
		return get(21,2).toString();
	}

	public void setC22(String value) {
		 set(21,2,value);
	}
	public String getD22() {
		return get(21,3).toString();
	}

	public void setD22(String value) {
		 set(21,3,value);
	}
	public String getE22() {
		return get(21,4).toString();
	}

	public void setE22(String value) {
		 set(21,4,value);
	}
	public String getF22() {
		return get(21,5).toString();
	}

	public void setF22(String value) {
		 set(21,5,value);
	}
	public String getG22() {
		return get(21,6).toString();
	}

	public void setG22(String value) {
		 set(21,6,value);
	}
	public String getH22() {
		return get(21,7).toString();
	}

	public void setH22(String value) {
		 set(21,7,value);
	}
	public String getA23() {
		return get(22,0).toString();
	}

	public void setA23(String value) {
		 set(22,0,value);
	}
	public String getB23() {
		return get(22,1).toString();
	}

	public void setB23(String value) {
		 set(22,1,value);
	}
	public String getC23() {
		return get(22,2).toString();
	}

	public void setC23(String value) {
		 set(22,2,value);
	}
	public String getD23() {
		return get(22,3).toString();
	}

	public void setD23(String value) {
		 set(22,3,value);
	}
	public String getE23() {
		return get(22,4).toString();
	}

	public void setE23(String value) {
		 set(22,4,value);
	}
	public String getF23() {
		return get(22,5).toString();
	}

	public void setF23(String value) {
		 set(22,5,value);
	}
	public String getG23() {
		return get(22,6).toString();
	}

	public void setG23(String value) {
		 set(22,6,value);
	}
	public String getH23() {
		return get(22,7).toString();
	}

	public void setH23(String value) {
		 set(22,7,value);
	}
	public String getA24() {
		return get(23,0).toString();
	}

	public void setA24(String value) {
		 set(23,0,value);
	}
	public String getB24() {
		return get(23,1).toString();
	}

	public void setB24(String value) {
		 set(23,1,value);
	}
	public String getC24() {
		return get(23,2).toString();
	}

	public void setC24(String value) {
		 set(23,2,value);
	}
	public String getD24() {
		return get(23,3).toString();
	}

	public void setD24(String value) {
		 set(23,3,value);
	}
	public String getE24() {
		return get(23,4).toString();
	}

	public void setE24(String value) {
		 set(23,4,value);
	}
	public String getF24() {
		return get(23,5).toString();
	}

	public void setF24(String value) {
		 set(23,5,value);
	}
	public String getG24() {
		return get(23,6).toString();
	}

	public void setG24(String value) {
		 set(23,6,value);
	}
	public String getH24() {
		return get(23,7).toString();
	}

	public void setH24(String value) {
		 set(23,7,value);
	}
	public String getA25() {
		return get(24,0).toString();
	}

	public void setA25(String value) {
		 set(24,0,value);
	}
	public String getB25() {
		return get(24,1).toString();
	}

	public void setB25(String value) {
		 set(24,1,value);
	}
	public String getC25() {
		return get(24,2).toString();
	}

	public void setC25(String value) {
		 set(24,2,value);
	}
	public String getD25() {
		return get(24,3).toString();
	}

	public void setD25(String value) {
		 set(24,3,value);
	}
	public String getE25() {
		return get(24,4).toString();
	}

	public void setE25(String value) {
		 set(24,4,value);
	}
	public String getF25() {
		return get(24,5).toString();
	}

	public void setF25(String value) {
		 set(24,5,value);
	}
	public String getG25() {
		return get(24,6).toString();
	}

	public void setG25(String value) {
		 set(24,6,value);
	}
	public String getH25() {
		return get(24,7).toString();
	}

	public void setH25(String value) {
		 set(24,7,value);
	}
	public String getA26() {
		return get(25,0).toString();
	}

	public void setA26(String value) {
		 set(25,0,value);
	}
	public String getB26() {
		return get(25,1).toString();
	}

	public void setB26(String value) {
		 set(25,1,value);
	}
	public String getC26() {
		return get(25,2).toString();
	}

	public void setC26(String value) {
		 set(25,2,value);
	}
	public String getD26() {
		return get(25,3).toString();
	}

	public void setD26(String value) {
		 set(25,3,value);
	}
	public String getE26() {
		return get(25,4).toString();
	}

	public void setE26(String value) {
		 set(25,4,value);
	}
	public String getF26() {
		return get(25,5).toString();
	}

	public void setF26(String value) {
		 set(25,5,value);
	}
	public String getG26() {
		return get(25,6).toString();
	}

	public void setG26(String value) {
		 set(25,6,value);
	}
	public String getH26() {
		return get(25,7).toString();
	}

	public void setH26(String value) {
		 set(25,7,value);
	}
	public String getA27() {
		return get(26,0).toString();
	}

	public void setA27(String value) {
		 set(26,0,value);
	}
	public String getB27() {
		return get(26,1).toString();
	}

	public void setB27(String value) {
		 set(26,1,value);
	}
	public String getC27() {
		return get(26,2).toString();
	}

	public void setC27(String value) {
		 set(26,2,value);
	}
	public String getD27() {
		return get(26,3).toString();
	}

	public void setD27(String value) {
		 set(26,3,value);
	}
	public String getE27() {
		return get(26,4).toString();
	}

	public void setE27(String value) {
		 set(26,4,value);
	}
	public String getF27() {
		return get(26,5).toString();
	}

	public void setF27(String value) {
		 set(26,5,value);
	}
	public String getG27() {
		return get(26,6).toString();
	}

	public void setG27(String value) {
		 set(26,6,value);
	}
	public String getH27() {
		return get(26,7).toString();
	}

	public void setH27(String value) {
		 set(26,7,value);
	}
	public String getA28() {
		return get(27,0).toString();
	}

	public void setA28(String value) {
		 set(27,0,value);
	}
	public String getB28() {
		return get(27,1).toString();
	}

	public void setB28(String value) {
		 set(27,1,value);
	}
	public String getC28() {
		return get(27,2).toString();
	}

	public void setC28(String value) {
		 set(27,2,value);
	}
	public String getD28() {
		return get(27,3).toString();
	}

	public void setD28(String value) {
		 set(27,3,value);
	}
	public String getE28() {
		return get(27,4).toString();
	}

	public void setE28(String value) {
		 set(27,4,value);
	}
	public String getF28() {
		return get(27,5).toString();
	}

	public void setF28(String value) {
		 set(27,5,value);
	}
	public String getG28() {
		return get(27,6).toString();
	}

	public void setG28(String value) {
		 set(27,6,value);
	}
	public String getH28() {
		return get(27,7).toString();
	}

	public void setH28(String value) {
		 set(27,7,value);
	}
	


}







