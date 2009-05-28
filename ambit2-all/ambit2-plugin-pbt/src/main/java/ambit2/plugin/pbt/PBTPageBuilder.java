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
import java.awt.Component;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.text.NumberFormatter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFPatternFormatting;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.ui.editors.Panel2D;
import ambit2.ui.jmol.Panel3D;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
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
    private static String getLayoutString(int repeat,String spec) {
    	StringBuffer b = new StringBuffer();
    	for (int i=0; i < repeat; i++) {
    		if (i>0) b.append(",");
    		b.append(spec);
    	}
    	return b.toString();
    }
    public static JPanel buildPanel(PBTTableModel model) {
    	return buildPanel(model,0,0);
    }
    public static JPanel buildPanel(PBTTableModel model, int rowOffset, int colOffset) {
        FormLayout layout = new FormLayout(
                getLayoutString(model.getColumnCount()+colOffset,"10dlu"),
                getLayoutString(model.getRowCount()+rowOffset,"pref"));
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

    public static JPanel buildPanel(PBTWorksheet worksheet) {
    	int colOffset = 0;
        FormLayout layout = new FormLayout(
                getLayoutString(worksheet.getMaxCol()+1,"100dlu:grow"),
                getLayoutString(worksheet.getMaxRow()+worksheet.rowOffset+1,"pref:grow"));
        
        
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();  
        cc.insets = new Insets(1,1,1,1);
        cc.hAlign = CellConstraints.DEFAULT;

        PresentationModel<PBTWorksheet> model = new PresentationModel<PBTWorksheet>(worksheet) {
       
        };

        Iterator<HSSFRow> rows = worksheet.getSheet().rowIterator();
		while (rows.hasNext()) {
			HSSFRow row = rows.next();
			
			int rowNum = row.getRowNum();
			Iterator<HSSFCell> cells = row.cellIterator();
			while (cells.hasNext()) {
	        	JComponent c = null;				
				HSSFCell cell = cells.next();
				
				short dataFormat = cell.getCellStyle().getDataFormat();
				
				if ((cell.getRowIndex()+ worksheet.rowOffset) < 0) continue;
				if ((cell.getColumnIndex()+ colOffset) < 0) continue;				
				if (cell.getRowIndex()>= worksheet.getMaxRow()) continue;
				if (cell.getColumnIndex()>= worksheet.getMaxCol()) continue;
				
				boolean hidden = false;
				int colspan = 1;
				int rowspan = 1;
		        for (int i=0; i < worksheet.getSheet().getNumMergedRegions();i++) {
					CellRangeAddress merged = worksheet.getSheet().getMergedRegion(i);
					if ((cell.getRowIndex() >= merged.getFirstRow()) && (cell.getRowIndex() <= merged.getLastRow()) && 
						(cell.getColumnIndex() >= merged.getFirstColumn()) && (cell.getColumnIndex() <= merged.getLastColumn())) {
						if ((cell.getRowIndex() == merged.getFirstRow()) && (cell.getColumnIndex() == merged.getFirstColumn())) {
							rowspan = merged.getLastRow() - merged.getFirstRow() +1;
							if (rowspan > worksheet.getMaxRow())
								rowspan = worksheet.getMaxRow() - merged.getFirstRow() + 1;
							colspan = merged.getLastColumn() - merged.getFirstColumn() +1;
							if (colspan > worksheet.getMaxCol())
								colspan = worksheet.getMaxCol()  - merged.getFirstColumn() +1;							
//							System.out.println("Cell " + merged.getFirstRow() + "," + merged.getFirstColumn() + "-" + merged.getLastRow() + "," + merged.getLastColumn());							
						} else {
							//System.out.println("Merged " + merged.getFirstRow() + "," + merged.getFirstColumn() + "-" + merged.getLastRow() + "," + merged.getLastColumn());							
							hidden = true;						
						}
					} else continue;

				}
		        //System.out.println(cell.getRowIndex() + "," + cell.getColumnIndex() + "\t" + cell.toString());		        
		        if (hidden) continue;

				HSSFPalette palette = worksheet.getWorkbook().getCustomPalette();
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
				Object extCell = worksheet.getExtendedCell(cell.getRowIndex()+1,cell.getColumnIndex()+1);
				String propertyName = PBTWorksheet.getCellName(cell.getRowIndex(),cell.getColumnIndex()).toLowerCase();
				//if (cell.getRowIndex()!=5) propertyName = "dummy";
				//Dimension d = new Dimension(140,20);	
				if ((colspan >= worksheet.getMaxCol()) && (cell.getRowIndex()>1)) { 
					colspan = worksheet.getMaxCol()-cell.getColumnIndex();
	        		JComponent separator = builder.addSeparator(cell.toString().trim(), 
	        				cc.xywh(cell.getColumnIndex()+colOffset+1,cell.getRowIndex()+worksheet.rowOffset+1,
	    	        				colspan,rowspan));
	        		separator.setAlignmentX(JComponent.CENTER_ALIGNMENT);
	        		
				} else if (extCell != null) {
					if (extCell instanceof List) { 
						c = BasicComponentFactory.createComboBox(new SelectionInList(((List)extCell),model.getModel(propertyName)));
						//c.setPreferredSize(d);	
		        		c.setBackground(background);
		        		c.setEnabled(true);
		        	} else if (extCell instanceof IAtomContainer) {
		        		JTabbedPane tab = new JTabbedPane();
		        		Panel2D p2d = new Panel2D();
		        		p2d.setAtomContainer((IAtomContainer)extCell,true);
		        		worksheet.addPropertyChangeListener("E"+worksheet.getCellName(cell.getRowIndex(), cell.getColumnIndex()).toLowerCase(),p2d);
		        		tab.addTab("Structure diagram", p2d);
		        		
		        		Panel3D p3d = new Panel3D();
		        		p3d.setObject((IAtomContainer)extCell);
		        		worksheet.addPropertyChangeListener("E"+worksheet.getCellName(cell.getRowIndex(), cell.getColumnIndex()).toLowerCase(),p3d);
		        		tab.addTab("3D", p3d);
		        		c = tab;
		        	} else if (extCell instanceof WorksheetAction) {
		        		((WorksheetAction)extCell).setWorksheet(worksheet);
						c = createLabel(cell.toString(),(WorksheetAction)extCell);
						//c.setBackground(background);		        		
					} else {
						if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
							c = BasicComponentFactory.createLabel(model.getModel(propertyName));
			        		c.setEnabled(true);
			        		c.setForeground(Color.RED);
			        		c.setBackground(builder.getPanel().getBackground());
			        		c.setBorder(null);
						} else 
							c = createLabel(cell.toString(),null);
						
					}						
				} else 
				switch (cell.getCellType()) {
				case  HSSFCell.CELL_TYPE_STRING: {
					c = createLabel(cell.toString(),null);
	        		break;	        		
				}					
				case  HSSFCell.CELL_TYPE_FORMULA: {
			        JTextField textField = new JTextField() {
			        	@Override
			        	public String getToolTipText() {
			        		if ("".equals(getText()))
			        			return "This is an automatically calculated value";
			        		else return getText();
			        	}
			        };
			        ToolTipManager.sharedInstance().registerComponent(textField);
			        Bindings.bind(textField,model.getModel(propertyName),true);						
					c = textField;					
	        		c.setEnabled(false);
	        		c.setBackground(background);
	        		c.setBorder(BorderFactory.createEtchedBorder());
	        		break;					
				}
				default:
					switch (dataFormat) {
					//Text
					case 0x31 : {
					    c = createTextComponent(rowspan, model, propertyName,  background,"Enter text");
					    break;

					}
					//General
					case 0: {
					    c = createTextComponent(rowspan, model, propertyName,  background, "Enter text");
					    break;						
					} 
					//numeric "0.00E+00"
					case 0xb: {
					    c = createNumberComponent(rowspan, model, propertyName,  background, "Enter number",2);
					    break;								
					}
					//numeric "0.0"
					case 0xb2: {
					    c = createNumberComponent(rowspan, model, propertyName,  background, "Enter number",1);
					    break;											
					}
					//numeric "0.00"
					case 0x2: {
					    c = createNumberComponent(rowspan, model, propertyName,  background, "Enter number",2);
					    break;											
					}					
					default: {
						System.out.println(dataFormat);
					}
					}
				}
	        	if (c != null) {
	        		if (cell.getCellComment() != null)
	        				c.setToolTipText(cell.getCellComment().getString().getString());
	        		c.setAlignmentX(component_alignment);
	        		if (colspan >= worksheet.getMaxCol())
						colspan = worksheet.getMaxCol()-cell.getColumnIndex();
	        		builder.add(c,cc.xywh(cell.getColumnIndex()+colOffset+1,cell.getRowIndex()+worksheet.rowOffset+1,
	        				colspan,rowspan));
	        	}
				
			}
		}
        builder.setBorder(BorderFactory.createEtchedBorder());
        return builder.getPanel();
    }       
    protected static JComponent createTextComponent(int rowspan, PresentationModel<PBTWorksheet> model, String propertyName, Color background, final String tooltip) {
    	JComponent c = null;
		if (rowspan==1) {
	        JTextField textField = new JTextField() {
	        	@Override
	        	public String getToolTipText() {
	        		if ("".equals(getText())) return tooltip;
	        		else return getText();
	        	}
	        };
	        Bindings.bind(textField,model.getModel(propertyName),true);		
	        ToolTipManager.sharedInstance().registerComponent(textField);				        
			c = textField;
			c.setBackground(background);
		} else {
			JTextArea textArea = BasicComponentFactory.createTextArea(model.getModel(propertyName),true);
			textArea.setBackground(background);
			c = new JScrollPane(textArea);
		}
    	c.setEnabled(true);
    	c.setBorder(loweredBorder);
    	return c;
    }
    protected static JComponent createNumberComponent(int rowspan, PresentationModel<PBTWorksheet> model, 
    				String propertyName, Color background, final String tooltip, int fractionDigits) {
    	NumberFormat format = DecimalFormat.getInstance();
    	NumberFormatter nf = new NumberFormatter(format) {
    	    public String valueToString(Object iv) throws ParseException {
    	        if ((iv == null) || (iv.equals(Double.NaN))) {
    	            return "";
    	        }
    	        else {
    	            return super.valueToString(iv);
    	        }
    	    }
    	    public Object stringToValue(String text) throws ParseException {
    	        if ("".equals(text)) {
    	            return null;
    	        }
    	        return super.stringToValue(text);
    	    }
    	};
    	
    	format.setMaximumFractionDigits(fractionDigits);
        JFormattedTextField c = new JFormattedTextField(nf) {
	        	@Override
	        	public String getToolTipText() {
	        		if ("".equals(getText())) return tooltip;
	        		else return getText();
	        	}

	        };
	    Bindings.bind(c,model.getModel(propertyName));		
	    ToolTipManager.sharedInstance().registerComponent(c);				        
		c.setBackground(Color.orange);

    	c.setEnabled(true);
    	c.setBorder(loweredBorder);
    	return c;
    }    
    protected static JComponent createLabel(String string, WorksheetAction action) {
    	String t = "???";
    	if (string != null)
    		t = "<html><body>"+string.trim().replace("\n", "<br>")+"</body></html>";
    	
		JComponent c;
		if (action == null)
			c = new JLabel(t);
		else {
			action.putValue(Action.NAME, string);
			c = new JButton(action);
		}
		c.setOpaque(true);
		c.setAlignmentX(Component.RIGHT_ALIGNMENT);
		//c.setBackground(background);
		//c.setBorder(risedBorder);
		return c;
    }
}
