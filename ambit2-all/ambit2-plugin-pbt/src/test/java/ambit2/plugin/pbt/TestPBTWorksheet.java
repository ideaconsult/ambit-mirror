package ambit2.plugin.pbt;


import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.apache.poi.hssf.record.DVRecord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.core.io.FileOutputState;
import ambit2.plugin.pbt.PBTWorkBook.WORKSHEET_INDEX;
import ambit2.plugin.pbt.processors.PBTExporter;


public class TestPBTWorksheet {
	protected PBTWorkBook workbook;
	protected InputStream in;
	
	@Before
	public void setup() throws Exception {
 	    workbook = new PBTWorkBook();
		 

	}
	@After
	public void cleanup() throws Exception {
		workbook = null;
	}

	@Test
	public void testEvaluateTSheet() throws Exception {
		PBTWorksheet ws = workbook.getWorksheet(WORKSHEET_INDEX.Toxicity);
		PBTWorksheet result = workbook.getWorksheet(WORKSHEET_INDEX.RESULT);
		
		//ws.set(5,2,0.1);
		ws.setC6(0.1);
		Assert.assertEquals(0.1,ws.get(5,2));
		Assert.assertEquals("< =0.1 mg/L",ws.get(5,3));
		Assert.assertEquals("Toxic (T)", result.getC7());
		ws.set(5,2,10.0);
		Assert.assertEquals(10.0,ws.get(5,2));			
		Assert.assertEquals("> 0.1 mg/L",ws.get(5,3));
		Assert.assertEquals("Not toxic (not T)", result.getC7());

	}	
	@Test
	public void testProperties() throws Exception {
		
		int maxLen = 0;
		for (WORKSHEET_INDEX w : WORKSHEET_INDEX.values() ) {
			PBTWorksheet ws = workbook.getWorksheet(w);
			for (int r=0; r < ws.getMaxRow(); r++)
				for (int c=0; c < ws.getMaxCol(); c++) {
	
					Object value = ws.get(r,c);
					if ((value != null) && (!value.toString().equals(""))) {
						System.out.print(ws.getCellName(r,c));
						System.out.print("\t=\t");
						System.out.print("\"");	
						System.out.print(value);
						System.out.println("\"");	
						if (value.toString().length()>maxLen)
							maxLen = value.toString().length();
					}
	
				}
		}
		System.out.println(maxLen);
				
	}	
	@Test
	public void testRTF() throws Exception {
		setMolecule(MoleculeFactory.makeBenzene());
		PBTExporter exporter = new PBTExporter();
		exporter.setOutput(new FileOutputState(System.getProperty("user.home")+"/PBT.rtf"));
		FileOutputState file = exporter.process(workbook);
		Assert.assertTrue(file.getFile().exists());
	}
	@Test
	public void testPDF() throws Exception {
		setMolecule(MoleculeFactory.makeBenzene());
		PBTExporter exporter = new PBTExporter();
		exporter.setOutput(new FileOutputState(System.getProperty("user.home")+"/PBT.pdf"));
		FileOutputState file = exporter.process(workbook);
		Assert.assertTrue(file.getFile().exists());
	}
	@Test
	public void testHTML() throws Exception {
		setMolecule(MoleculeFactory.makeBenzene());
		PBTExporter exporter = new PBTExporter();
		exporter.setOutput(new FileOutputState(System.getProperty("user.home")+"/PBT.html"));
		FileOutputState file = exporter.process(workbook);
		Assert.assertTrue(file.getFile().exists());
	}	
	@Test
	public void testEvaluateSubstanceAntTSheet() throws Exception {
		Assert.assertEquals(78.1112,((Double)setMolecule(MoleculeFactory.makeBenzene())).doubleValue(),1E-4);

		PBTWorksheet s = workbook.getWorksheet(WORKSHEET_INDEX.SUBSTANCE);
		Assert.assertEquals("narcotic",s.get(15,1));
		PBTWorksheet t = workbook.getWorksheet(WORKSHEET_INDEX.Toxicity);
		//Assert.assertEquals(10,t.get(17,1));//Critical body burden
		Assert.assertEquals(0.156222,(Double)t.get(17,0),1E-6);
	
		
		PBTWorksheet result = workbook.getWorksheet(WORKSHEET_INDEX.RESULT);
		Assert.assertEquals("\"T Assessment\" failed",result.getC7());
		t.setC6(0.1);
		Assert.assertEquals("Toxic (T)",result.getC7());
		
	}	
	public Object setMolecule(IMolecule molecule) throws Exception {
		PBTWorksheet ws = workbook.getWorksheet(WORKSHEET_INDEX.SUBSTANCE);
		
		ws.setExtendedCell(molecule,PBTWorkBook.ROW_STRUCTURE,PBTWorkBook.COLUMN_STRUCTURE);
		Assert.assertTrue(ws.getExtendedCellValue(PBTWorkBook.ROW_STRUCTURE,PBTWorkBook.COLUMN_STRUCTURE) instanceof IMolecule);
		Assert.assertEquals(molecule.getAtomCount(),((IMolecule)ws.getExtendedCellValue(PBTWorkBook.ROW_STRUCTURE,PBTWorkBook.COLUMN_STRUCTURE)).getAtomCount());
		Object o = ws.getExtendedCellValue(11,0);
		Assert.assertTrue(o instanceof WorksheetAction);
		((WorksheetAction)o).setWorksheet(ws);
		((WorksheetAction)o).actionPerformed(null);
		int targetRow = ((WorksheetAction)o).getResultRow();
		int targetCol = ((WorksheetAction)o).getResultCol();
		ws.set(targetRow+2,targetCol,100);
		ws.set(targetRow+3,targetCol,10);
		ws.set(targetRow+4,targetCol,"narcotic");
		return ws.get(targetRow,targetCol);
		
	}		
	@Test
	public void test() throws Exception {
		
		PBTWorksheet ws = workbook.getWorksheet(WORKSHEET_INDEX.SUBSTANCE);
		HSSFSheet sheet = ws.getSheet();
		Iterator<HSSFRow> rows = sheet.rowIterator();
		while (rows.hasNext()) {
			HSSFRow row = rows.next();
			
			int rowNum = row.getRowNum();
			Iterator<HSSFCell> cells = row.cellIterator();
			while (cells.hasNext()) {
				HSSFCell cell = cells.next();
				System.out.print(Character.toChars(65+cell.getColumnIndex()));
				System.out.print(rowNum+1);
				//System.out.print(col);				
				System.out.print('\t');
				System.out.print('\"');
				System.out.print(cell.getCellStyle().getDataFormatString());
				System.out.print('\"');
				System.out.print('\t');
				System.out.print('\"');				
				switch (cell.getCellType()) {
				case  HSSFCell.CELL_TYPE_BLANK: {
					System.out.print("Blank");
					break;
				}
				case  HSSFCell.CELL_TYPE_BOOLEAN: {
					System.out.print("boolean");
					break;
				}		
				case  HSSFCell.CELL_TYPE_STRING: {
					System.out.print("string");
					break;
				}			
				case  HSSFCell.CELL_TYPE_NUMERIC: {
					System.out.print("numeric");
					break;
				}				
				case  HSSFCell.CELL_TYPE_ERROR: {
					System.out.print("error");
					break;
				}				
				case  HSSFCell.CELL_TYPE_FORMULA: {
					try {
						CellValue value = ws.evaluate(cell);
						System.out.print(value.getStringValue());
					} catch (Exception x) {
						System.out.print(x.getMessage());
					}
					System.out.print('\"');		
					System.out.print('\t');
					System.out.print('\"');		
					System.out.print(cell.getCellFormula());	
					break;
				}
				default:
					System.out.print(cell.toString());
				}
				System.out.println('\"');

			}
		}
		
/*
Data validation is not yet supported.  plan to include it in 3.5-final
 */
		List<DVRecord> list = sheet.getDVRecords();
		for (int i =0; i < list.size(); i++) {
			DVRecord record = list.get(i);
			System.out.println(record);
		}
		
		for (int i=0; i < sheet.getNumMergedRegions();i++) {
			CellRangeAddress merged = sheet.getMergedRegion(i);
			System.out.print(merged.getFirstRow());
			System.out.print(',');
			System.out.print(merged.getFirstColumn());
			System.out.print('-');
			System.out.print(merged.getLastRow());
			System.out.print(',');
			System.out.print(merged.getLastColumn());			
			System.out.println();
		}

	
	}
	@Test
	public void testCellName() throws Exception {
		Assert.assertEquals("A1",PBTWorksheet.getCellName(0,0));
		Assert.assertEquals("Z100",PBTWorksheet.getCellName(99,25));
		Assert.assertEquals("AA100",PBTWorksheet.getCellName(99,26));
		Assert.assertEquals("BA100",PBTWorksheet.getCellName(99,52));
		
		Assert.assertEquals("B12",PBTWorksheet.getCellName(11,1));
		Assert.assertEquals("B10",PBTWorksheet.getCellName(9,1));
		Assert.assertNull(PBTWorksheet.getCellName(-1,52));
	
		for (int row=0; row < 28; row++) 
			for (int col=0; col < 8; col++) {
				System.out.print("public String get");
				System.out.print(PBTWorksheet.getCellName(row,col));
				System.out.println("() {");
				System.out.print("\treturn get(");
				System.out.print(row);
				System.out.print(",");
				System.out.print(col);				
				System.out.println(").toString();");
				System.out.println("}");
				
				System.out.println();	
				
				System.out.print("public void set");
				System.out.print(PBTWorksheet.getCellName(row,col));
				System.out.println("(String value) {");
				System.out.print("\t set(");
				System.out.print(row);
				System.out.print(",");
				System.out.print(col);
				System.out.print(",value");
				System.out.println(");");
				System.out.println("}");				
			}
	}
	/*
	public Document toXMLTemplate(PBTWorksheet worksheet) throws Exception {
        Iterator<HSSFRow> rows = worksheet.getSheet().rowIterator();
		while (rows.hasNext()) {
			HSSFRow row = rows.next();
			
			int rowNum = row.getRowNum();
			Iterator<HSSFCell> cells = row.cellIterator();
			while (cells.hasNext()) {
	        	JComponent c = null;				
				HSSFCell cell = cells.next();
				
				if ((cell.getRowIndex()) < 0) continue;
				if ((cell.getColumnIndex()) < 0) continue;				
				if (cell.getRowIndex()>= worksheet.getMaxRow()) continue;
				if (cell.getColumnIndex()>= worksheet.getMaxCol()) continue;
				
				boolean hidden = false;
				int colspan = 1;
				int rowspan = 1;
		        for (int i=0; i < worksheet.getSheet().getNumMergedRegions();i++) {
					CellRangeAddress merged = worksheet.getSheet().getMergedRegion(i);
  
					if ((cell.getRowIndex() >= merged.getFirstRow()) && (cell.getRowIndex() <= merged.getLastRow())) {
						if (cell.getRowIndex() == merged.getFirstRow()) {
							rowspan = merged.getLastRow() - merged.getFirstRow() +1;
							if (rowspan > worksheet.getMaxRow())
								rowspan = worksheet.getMaxRow();
						} else hidden = true;						
					} else continue;
					
					if ((cell.getColumnIndex() >= merged.getFirstColumn()) && (cell.getColumnIndex() <= merged.getLastColumn())) {
						if (cell.getColumnIndex() == merged.getFirstColumn()) {
							colspan = merged.getLastColumn() - merged.getFirstColumn() +1;
							if (colspan > worksheet.getMaxCol())
								colspan = worksheet.getMaxCol()  - merged.getFirstColumn() +1;
						} else hidden = true;						
					} else continue;					
				}				
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
				
				String propertyName = PBTWorksheet.getCellName(cell.getRowIndex(),cell.getColumnIndex()).toLowerCase();
				//if (cell.getRowIndex()!=5) propertyName = "dummy";
				
				if ((colspan >= worksheet.getMaxCol()) && (cell.getRowIndex()>1)) { 
	        		JComponent separator = builder.addSeparator(cell.toString().trim(), 
	        				cc.xywh(cell.getColumnIndex()+colOffset,cell.getRowIndex()+rowOffset,
	    	        				colspan,rowspan));
	        		separator.setAlignmentX(JComponent.CENTER_ALIGNMENT);

				} else
				switch (cell.getCellType()) {
				case  HSSFCell.CELL_TYPE_BLANK: {
					
	        		c = BasicComponentFactory.createTextField(model.getModel(propertyName),true);
	        		c.setBackground(background);
	        		c.setEnabled(true);
	        		c.setBorder(loweredBorder);

	        		break;
				}
				case  HSSFCell.CELL_TYPE_BOOLEAN: {
					c = BasicComponentFactory.createTextField(model.getModel(propertyName),true);
	        		c.setBackground(inputColor);
	        		c.setEnabled(true);
	        		c.setBorder(loweredBorder);
	        		break;
				}		
				case  HSSFCell.CELL_TYPE_STRING: {
	        		//c = BasicComponentFactory.createTextField(valueModel,true);
	        		c = new JLabel("<html>"+cell.toString().replace("\\n", "<br>")+"</html>");
	        		c.setOpaque(true);
	        		//c.setBackground(background);
	        		c.setBorder(risedBorder);
	        		break;	        		
				}			
				case  HSSFCell.CELL_TYPE_NUMERIC: {
	        		//c = new JFormattedTextField(cell.getNumericCellValue());
	        		c = BasicComponentFactory.createTextField(model.getModel(propertyName),true);
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
					
					c = BasicComponentFactory.createTextField(model.getModel(propertyName),true);
	        		c.setEnabled(false);
	        		c.setBackground(background);
	        		c.setBorder(BorderFactory.createEtchedBorder());
	        		c.setToolTipText("This is an automatically calculated value");
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
	}
	*/
}
