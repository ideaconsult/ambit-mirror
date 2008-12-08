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

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.jgoodies.binding.beans.Model;

public class PBTWorksheet extends Model {
	public HSSFWorkbook getWorkbook() {
		return workbook;
	}
	protected HSSFWorkbook workbook;
	protected HSSFSheet sheet;
	public HSSFSheet getSheet() {
		return sheet;
	}
	protected HSSFFormulaEvaluator formulaEvaluator;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5725226752787199270L;
	public PBTWorksheet(HSSFWorkbook workbook, String sheetName) {
		this.workbook = workbook;
		sheet = workbook.getSheet(sheetName);
		formulaEvaluator = new HSSFFormulaEvaluator(workbook);
	}

    
	
}





