package ambit2.plugin.pbt;


import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.apache.poi.hssf.record.DVRecord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestPBTWorksheet {
	protected HSSFWorkbook workbook ;
	protected InputStream in;
	
	@Before
	public void setup() throws Exception {
		String file = "2008-12-03_REACH PBT Screening Tool_V0.99b_U N P R O T E C T E D.xls";
		in = PBTWorkflow.class.getClassLoader().getResourceAsStream("ambit2/plugin/pbt/xml/"+file);
		POIFSFileSystem poifsFileSystem = new POIFSFileSystem(in);
		
		 workbook = new HSSFWorkbook(poifsFileSystem);

	}
	@After
	public void cleanup() throws Exception {
		in.close();
	}
	@Test
	public void testProxy() throws Exception {
		//WorksheetProxyFactory.getProxy(null,workbook.getSheet("T-Sheet"));
	}
	@Test
	public void testEvaluate() throws Exception {
		PBTWorksheet ws = new PBTWorksheet(workbook,"T-Sheet");
		//ws.set(5,2,0.1);
		ws.setC6("0.1");
		Assert.assertEquals(0.1,ws.get(5,2));
		Assert.assertEquals("< =0.1 mg/L",ws.get(5,3));
		ws.set(5,2,10.0);
		Assert.assertEquals(10.0,ws.get(5,2));		
		Assert.assertEquals("> 0.1 mg/L",ws.get(5,3));
	}	
	@Test
	public void test() throws Exception {
		
        

		HSSFSheet sheet = workbook.getSheet("P-Sheet");
		
		HSSFFormulaEvaluator formulaEvaluator = new HSSFFormulaEvaluator(workbook);

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
						CellValue value = formulaEvaluator.evaluate(cell);
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
	public Object getA1() {
		return get(i,i);
	}
	public void setA1(Object value) {
		set(i,i,value);
	}
	*/

}
