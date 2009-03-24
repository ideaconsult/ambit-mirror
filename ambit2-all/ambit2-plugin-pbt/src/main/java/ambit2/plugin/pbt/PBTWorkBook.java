package ambit2.plugin.pbt;

import java.awt.Color;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.util.CellRangeAddress;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.MoleculeReader;

import com.jgoodies.binding.PresentationModel;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;


public class PBTWorkBook {
	
	public static final String PBT_TITLE = "REACH PBT SCREENING TOOL FOR AMBIT XT v.1.0.0";

	public static final String PBT_WORKBOOK = "ambit2.plugin.pbt.WORKBOOK";
	protected static final String PBT_CLARIANT="ambit2/plugin/pbt/xml/pbt_1_00.xls";
	final protected HSSFWorkbook workbook; 
	final protected InputStream workbook_stream;
	final protected POIFSFileSystem poifsFileSystem;
	final PBTWorksheet[] pbt_worksheets; 
	public static enum WORKSHEET_INDEX  {WELCOME,SUBSTANCE,Persistence,Bioaccumulation,Toxicity,RESULT};
	protected IStructureRecord record  = null;
    protected static Object[][] defs = {
    	{"TERMS & CONDITIONS",new Integer(27),new Integer(3),"ambit2/plugin/pbt/xml/welcome.xml"},   	
    	{"SUBSTANCE",new Integer(28),new Integer(6),"ambit2/plugin/pbt/xml/substance_page.xml"},

    	{"P-Sheet",new Integer(20),new Integer(6),"ambit2/plugin/pbt/xml/p_page.xml"},
    	{"B-Sheet",new Integer(22),new Integer(6),"ambit2/plugin/pbt/xml/b_page.xml"},
    	{"T-Sheet",new Integer(19),new Integer(6),"ambit2/plugin/pbt/xml/t_page.xml"},
    	{"Result",new Integer(15),new Integer(5),"ambit2/plugin/pbt/xml/result_page.xml"}
    };
    public PBTWorkBook() throws Exception {
    	this(PBT_CLARIANT);
    }
    public PBTWorkBook(String file) throws Exception {
		workbook_stream = PBTWorkBook.class.getClassLoader().getResourceAsStream(file);
		if (workbook_stream==null)
			throw new Exception("Can't find "+file);
		poifsFileSystem = new POIFSFileSystem(workbook_stream);	
		workbook = new HSSFWorkbook(poifsFileSystem);
		pbt_worksheets = new PBTWorksheet[defs.length];
		for (int i=0; i < defs.length;i++)
			pbt_worksheets[i] = createSheet(workbook,i);
	}
    public String getTitle(int index) {
    	return workbook.getSheetName(index);
    }    
    public PBTWorksheet getWorksheet(WORKSHEET_INDEX index) {
    	return getWorksheet(index.ordinal());
    }    
    protected PBTWorksheet getWorksheet(int index) {
    	if (index < pbt_worksheets.length)
    		return pbt_worksheets[index];
    	else return null;
    }
    public int size() {
    	return pbt_worksheets.length;
    }
    
    protected PBTWorksheet createSheet(HSSFWorkbook workbook,int index) {
		try {
	
			PBTWorksheet ws =  new PBTWorksheet(workbook,
					defs[index][0].toString(),
					(Integer)defs[index][1],
					(Integer)defs[index][2],
					defs[index][3].toString());
			return ws;
		} catch (Throwable x) {
			x.printStackTrace();
			return null;
		}
    }
    
    @Override
    protected void finalize() throws Throwable {
    	try {
    	if (workbook_stream != null)
    		workbook_stream.close();
    	} catch (Exception x) {
    		x.printStackTrace();
    	}
    	super.finalize();
    }
    protected com.lowagie.text.Cell getCell(PBTWorksheet worksheet, int rowIndex, int columnIndex) {
		int colspan = 1;
		int rowspan = 1;
		
        for (int i=0; i < worksheet.getSheet().getNumMergedRegions();i++) {
			CellRangeAddress merged = worksheet.getSheet().getMergedRegion(i);
			if ((rowIndex >= merged.getFirstRow()) && (rowIndex <= merged.getLastRow()) && 
				(columnIndex >= merged.getFirstColumn()) && (columnIndex <= merged.getLastColumn())) {
				if ((rowIndex == merged.getFirstRow()) && (columnIndex == merged.getFirstColumn())) {
					rowspan = merged.getLastRow() - merged.getFirstRow() +1;
					if (rowspan > worksheet.getMaxRow())
						rowspan = worksheet.getMaxRow() - merged.getFirstRow() + 1;
					colspan = merged.getLastColumn() - merged.getFirstColumn() +1;
					if (colspan > worksheet.getMaxCol())
						colspan = worksheet.getMaxCol()  - merged.getFirstColumn() +1;							
//					System.out.println("Cell " + merged.getFirstRow() + "," + merged.getFirstColumn() + "-" + merged.getLastRow() + "," + merged.getLastColumn());							
				} else {
					//System.out.println("Merged " + merged.getFirstRow() + "," + merged.getFirstColumn() + "-" + merged.getLastRow() + "," + merged.getLastColumn());							
					return null;					
				}
			} else continue;

		}    	
        Object value = worksheet.get(rowIndex,columnIndex);
        com.lowagie.text.Cell textCell = new com.lowagie.text.Cell(value.toString().replace('\r', ' ').replace('\n',' '));
        textCell.setRowspan(rowspan);
        textCell.setColspan(colspan);
        return textCell;
        
    }
    public void write(Document document) throws DocumentException {
    	int border = 0;
		for (WORKSHEET_INDEX w : WORKSHEET_INDEX.values() ) {
			PBTWorksheet ws = getWorksheet(w);
			Table table = new Table(ws.getMaxCol(),ws.getMaxRow());
			if (w == WORKSHEET_INDEX.WELCOME) {
				float[] f = {2f, 1f, 20f};
				table.setWidths(f);
				border = 0;
				table.setBorderWidth(1);
			} else {
				border = 1;
				table.setBorderWidth(1);
			}
			table.setAlignment(Element.ALIGN_LEFT);
			table.setBorderColor(Color.black);			
			table.setPadding(1);
			table.setSpacing(1);			
			for (int r=0; r < ws.getMaxRow(); r++) {

				for (int c=0; c < ws.getMaxCol(); c++) {
		
					com.lowagie.text.Cell cell = getCell(ws, r, c);
					
					if (cell == null) continue;
					cell.setBorder(border);
					
					if(cell.colspan() >= table.columns())
						cell.setColspan(table.columns());
					if(cell.rowspan() >= ws.getMaxRow())
						cell.setRowspan(ws.getMaxRow());					
					
					table.addCell(cell,r,c);
					//else 
						//table.addCell(new Cell(),r,c);
				}

			}
			if (w != WORKSHEET_INDEX.WELCOME) document.newPage();			
			document.add(table);

			document.add(new Paragraph(""));
			ws.setModified(false);
		}    	
		
    }
    
    public boolean isCompleted(WORKSHEET_INDEX index) {
    	switch (index) {
    	case WELCOME : return true;
    	case SUBSTANCE: {
    		PBTWorksheet ws = getWorksheet(index);
    		Object o = ws.getExtendedCell(10,5);
    		if ((o != null) && (o instanceof IMolecule) && (((IMolecule)o).getAtomCount()>0)) 
    			return true;
    		else return (!"".equals(ws.getB10())) && (!"".equals(ws.getB11()));
    	}
    	case Persistence: return !getWorksheet(index).getE20().trim().equals("due to insufficient / conflicting data");
    	case Bioaccumulation: return !getWorksheet(index).getD22().trim().equals("due to insufficient / conflicting data");
    	case Toxicity: return !getWorksheet(index).getD19().trim().equals("due to insufficient / conflicting data");
    	case RESULT: return !getWorksheet(index).getC9().trim().equals("PBT & vPvB Assessment failed");
    	default: return false;
    	}
    }
    public boolean isModified() {
    	for (WORKSHEET_INDEX w : WORKSHEET_INDEX.values() ) 
			if (getWorksheet(w).isModified()) return true;
		return false;

    }    
    public void setModified(boolean value) {
    	for (WORKSHEET_INDEX w : WORKSHEET_INDEX.values() ) 
			getWorksheet(w).setModified(value);    	
    }
    public void setRecord(IStructureRecord record) {
    	this.record = record;
    	MoleculeReader molreader = new MoleculeReader();
    	try {
    		setStructure(molreader.process(record));
    	} catch (Exception x) {
    		setStructure(null);
    	}
    }
    public IStructureRecord getRecord() {
    	return record;
    }
    public void setStructure(IAtomContainer structure) {
    	getWorksheet(1).setExtendedCell(structure, 10,5);    	
    }
    public IAtomContainer getStructure() {
    	Object a = getWorksheet(WORKSHEET_INDEX.SUBSTANCE).getExtendedCell(10,5);
    	if (a instanceof IAtomContainer)
    		return ((IAtomContainer)a);
    	else 
    		return null;
    }
    public void set(String fieldname,Object value) throws AmbitException {
    	
    	int i = fieldname.indexOf('_');
    	if (i <= 0) throw new AmbitException("Invalid fieldname");
    	
    	WORKSHEET_INDEX sheet = WORKSHEET_INDEX.valueOf(fieldname.substring(0,i));
    	fieldname = fieldname.substring(i+1);
    	PresentationModel<PBTWorksheet> model = new PresentationModel<PBTWorksheet>(getWorksheet(sheet));
    	model.setValue(fieldname.toLowerCase(), value.toString().trim());
    	
    }
}
