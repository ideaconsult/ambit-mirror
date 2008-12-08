/* PBTPage.java
 * Author: Nina Jeliazkova
 * Date: Oct 4, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.plugin.pbt;

import java.awt.Color;
import java.awt.Insets;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFPatternFormatting;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.util.CellRangeAddress;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


public class PBTPageBuilder {
    protected static Color inputColor = new Color(255,255,153);
    protected static Color labelColor = new Color(204,255,204);
    protected static Border loweredBorder = BorderFactory.createLoweredBevelBorder();
    protected static Border risedBorder = BorderFactory.createEmptyBorder();
    protected PBTPageBuilder() {
    }
    private static String getLayoutString(int repeat) {
    	StringBuffer b = new StringBuffer();
    	for (int i=0; i < repeat; i++) {
    		if (i>0) b.append(",");
    		b.append("pref");
    	}
    	return b.toString();
    }
    public static JPanel buildPanel(PBTTableModel model) {
    	return buildPanel(model,0,0);
    }
    public static JPanel buildPanel(PBTTableModel model, int rowOffset, int colOffset) {
        FormLayout layout = new FormLayout(
                getLayoutString(model.getColumnCount()+colOffset),
                getLayoutString(model.getRowCount()+rowOffset));
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();  
        cc.insets = new Insets(3,3,3,3);
        cc.hAlign = CellConstraints.DEFAULT;
        Enumeration<Cell> cells = model.getTable().keys();
        while (cells.hasMoreElements())  {
        	Cell cell = cells.nextElement();
        	if ((cell.row + rowOffset) <= 0) continue;
        	if ((cell.column + colOffset) <= 0) continue;
        	
        	Object o = model.getTable().get(cell);
        	JComponent c = null;
        	switch (cell.mode) {
        	case SECTION: {
        		builder.addSeparator(o.toString().trim(), 
        				cc.xyw(cell.column+colOffset,cell.row+rowOffset,cell.colspan));
        		break;
        	}
        	case TITLE: {
        		c = new JLabel("<html>"+o.toString().replace("\\n", "<br>")+"</html>");
        		c.setOpaque(true);
        		//c.setBackground(labelColor);
        		c.setBorder(risedBorder);
        		break;
        	}
        	case ERROR: {
        		/*wrappable labels*/
        		JTextPane txtMyTextPane= new JTextPane();
        		txtMyTextPane.setText("<html>"+o.toString().replace("\\n", "<br>")+"</html>");
        		txtMyTextPane.setBackground(null);
        		txtMyTextPane.setEditable(false);
        		txtMyTextPane.setBorder(null);
        		c = txtMyTextPane;
        		break; 
        		        	}        	
        	case LIST: {
        		c = BasicComponentFactory.createComboBox(new SelectionInList());
        		c.setBackground(inputColor);
        		break;
        	}
        	case FORMULA: {
        		//c = BasicComponentFactory.createTextField(valueModel,true);
        		c = new JTextField(o.toString());
        		c.setEnabled(false);
        		c.setBorder(BorderFactory.createEtchedBorder());
        		c.setToolTipText("This is an automatically calculated value");
        		break;
        	}
        	case INPUT: {
        		//c = BasicComponentFactory.createTextField(valueModel,true);
        		c = new JTextField(o.toString());
        		c.setBackground(inputColor);
        		c.setEnabled(true);
        		c.setBorder(loweredBorder);
        		break;
        	}        	
        	}
        	if (c != null)
        		builder.add(c,cc.xywh(cell.column+colOffset,cell.row+rowOffset,cell.colspan,cell.rowspan));
        }
        builder.setBorder(BorderFactory.createEtchedBorder());
        return builder.getPanel();
    }    

    public static JPanel buildPanel(HSSFWorkbook workbook, String sheetName, int rowOffset, int colOffset) {
    	int maxRow = 19;
    	int maxCol = 6;
        FormLayout layout = new FormLayout(
                getLayoutString(maxCol+colOffset),
                getLayoutString(maxRow+rowOffset));
        HSSFFormulaEvaluator formulaEvaluator = new HSSFFormulaEvaluator(workbook);
        HSSFSheet sheet = workbook.getSheet(sheetName);
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();  
        cc.insets = new Insets(3,3,3,3);
        cc.hAlign = CellConstraints.DEFAULT;


 
        Iterator<HSSFRow> rows = sheet.rowIterator();
		while (rows.hasNext()) {
			HSSFRow row = rows.next();
			
			int rowNum = row.getRowNum();
			Iterator<HSSFCell> cells = row.cellIterator();
			while (cells.hasNext()) {
	        	JComponent c = null;				
				HSSFCell cell = cells.next();
				
				if ((cell.getRowIndex()+ rowOffset) < 0) continue;
				if ((cell.getColumnIndex()+ colOffset) < 0) continue;				
				if (cell.getRowIndex()>=maxRow) continue;
				if (cell.getColumnIndex()>=maxCol) continue;
				
				boolean hidden = false;
				int colspan = 1;
				int rowspan = 1;
		        for (int i=0; i < sheet.getNumMergedRegions();i++) {
					CellRangeAddress merged = sheet.getMergedRegion(i);
  
					if ((cell.getRowIndex() >= merged.getFirstRow()) && (cell.getRowIndex() <= merged.getLastRow())) {
						if (cell.getRowIndex() == merged.getFirstRow()) {
							rowspan = merged.getLastRow() - merged.getFirstRow() +1;
						} else hidden = true;						
					} else continue;
					
					if ((cell.getColumnIndex() >= merged.getFirstColumn()) && (cell.getColumnIndex() <= merged.getLastColumn())) {
						if (cell.getColumnIndex() == merged.getFirstColumn()) {
							colspan = merged.getLastColumn() - merged.getFirstColumn() +1;
						} else hidden = true;						
					} else continue;					
				}				
		        if (hidden) continue;
		        
				HSSFPalette palette = workbook.getCustomPalette();
				HSSFColor color = palette.getColor(cell.getCellStyle().getFillForegroundColor());
				Color background = Color.white;
				if (HSSFPatternFormatting.NO_FILL != cell.getCellStyle().getFillPattern())
					background = new Color(color.getTriplet()[0],color.getTriplet()[1],color.getTriplet()[2]);
				
				float component_alignment;
				switch (cell.getCellStyle().getAlignment()) {
				case HSSFCellStyle.ALIGN_RIGHT: component_alignment = JComponent.RIGHT_ALIGNMENT;
				case HSSFCellStyle.ALIGN_CENTER: component_alignment = JComponent.CENTER_ALIGNMENT;
				default: 
					component_alignment = JComponent.LEFT_ALIGNMENT;
				}
				if ((colspan >= maxCol) && (cell.getRowIndex()>1)) { 
	        		JComponent separator = builder.addSeparator(cell.toString().trim(), 
	        				cc.xywh(cell.getColumnIndex()+colOffset,cell.getRowIndex()+rowOffset,
	    	        				colspan,rowspan));
	        		separator.setAlignmentX(JComponent.CENTER_ALIGNMENT);

				} else
				switch (cell.getCellType()) {
				case  HSSFCell.CELL_TYPE_BLANK: {
	        		c = new JTextField(cell.toString());
	        		c.setBackground(background);
	        		c.setEnabled(true);
	        		c.setBorder(loweredBorder);

	        		break;
				}
				case  HSSFCell.CELL_TYPE_BOOLEAN: {
	        		c = new JFormattedTextField(cell.getNumericCellValue());
	        		c.setBackground(inputColor);
	        		c.setEnabled(true);
	        		c.setBorder(loweredBorder);
	        		break;
				}		
				case  HSSFCell.CELL_TYPE_STRING: {
	        		//c = BasicComponentFactory.createTextField(valueModel,true);
					/*
	        		c = new JTextField(cell.toString());
	        		c.setBackground(inputColor);
	        		c.setEnabled(true);
	        		c.setBorder(loweredBorder);
	        		*/
	        		c = new JLabel("<html>"+cell.toString().replace("\\n", "<br>")+"</html>");
	        		c.setOpaque(true);
	        		//c.setBackground(background);
	        		c.setBorder(risedBorder);
	        		break;	        		
				}			
				case  HSSFCell.CELL_TYPE_NUMERIC: {
	        		c = new JFormattedTextField(cell.getNumericCellValue());
	        		c.setBackground(background);
	        		c.setEnabled(true);
	        		c.setBorder(loweredBorder);
	        		break;
				}				
				case  HSSFCell.CELL_TYPE_ERROR: {
					System.out.print("error");
					break;
				}				
				case  HSSFCell.CELL_TYPE_FORMULA: {
					

					
	        		c = new JTextField(cell.getCellFormula());
	        		c.setEnabled(false);
	        		c.setBackground(background);
	        		c.setBorder(BorderFactory.createEtchedBorder());
	        		c.setToolTipText("This is an automatically calculated value");
	        		
					try {
						CellValue value = formulaEvaluator.evaluate(cell);
						((JTextField)c).setText(value.getStringValue());
					} catch (Exception x) {
						System.out.print(x.getMessage());
					}	        		
	        		break;					
				}
				default:
					//System.out.print(cell.toString());
				}
	        	if (c != null) {
	        		if (cell.getCellComment() != null)
	        				c.setToolTipText(cell.getCellComment().getString().getString());
	        		c.setAlignmentX(component_alignment);
	        		builder.add(c,cc.xywh(cell.getColumnIndex()+colOffset,cell.getRowIndex()+rowOffset,
	        				colspan,rowspan));
	        	}
				
			}
		}
/*
        	switch (cell.mode) {
        	case SECTION: {
        		builder.addSeparator(o.toString().trim(), 
        				cc.xyw(cell.column+colOffset,cell.row+rowOffset,cell.colspan));
        		break;
        	}
        	case TITLE: {
        		c = new JLabel("<html>"+o.toString().replace("\\n", "<br>")+"</html>");
        		c.setOpaque(true);
        		//c.setBackground(labelColor);
        		c.setBorder(risedBorder);
        		break;
        	}
        	case ERROR: {
        		JTextPane txtMyTextPane= new JTextPane();
        		txtMyTextPane.setText("<html>"+o.toString().replace("\\n", "<br>")+"</html>");
        		txtMyTextPane.setBackground(null);
        		txtMyTextPane.setEditable(false);
        		txtMyTextPane.setBorder(null);
        		c = txtMyTextPane;
        		break; 
        		        	}        	
        	case LIST: {
        		c = BasicComponentFactory.createComboBox(new SelectionInList());
        		c.setBackground(inputColor);
        		break;
        	}
        	case FORMULA: {
        		//c = BasicComponentFactory.createTextField(valueModel,true);
        		c = new JTextField(o.toString());
        		c.setEnabled(false);
        		c.setBorder(BorderFactory.createEtchedBorder());
        		c.setToolTipText("This is an automatically calculated value");
        		break;
        	}
        	case INPUT: {
        		//c = BasicComponentFactory.createTextField(valueModel,true);
        		c = new JTextField(o.toString());
        		c.setBackground(inputColor);
        		c.setEnabled(true);
        		c.setBorder(loweredBorder);
        		break;
        	}        	
        	}
        	if (c != null)
        		builder.add(c,cc.xywh(cell.column+colOffset,cell.row+rowOffset,cell.colspan,cell.rowspan));
        }
*/        
        builder.setBorder(BorderFactory.createEtchedBorder());
        return builder.getPanel();
    }        
}
