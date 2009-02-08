package ambit2.plugin.pbt;

import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;


public class PBTWorkBook {
	protected static final String PBT_CLARIANT="2008-12-03_REACH PBT Screening Tool_V0.99b_U N P R O T E C T E D.xls";
	final protected HSSFWorkbook workbook; 
	final protected InputStream workbook_stream;
	final protected POIFSFileSystem poifsFileSystem;
	final PBTWorksheet[] pbt_worksheets; 
	
    protected static Object[][] defs = {
    	{"TERMS & CONDITIONS",new Integer(27),new Integer(3),"ambit2/plugin/pbt/xml/welcome.xml"},   	
    	{"SUBSTANCE",new Integer(22),new Integer(6),"ambit2/plugin/pbt/xml/substance_page.xml"},
    	{"P-Sheet",new Integer(20),new Integer(6),"ambit2/plugin/pbt/xml/p_page.xml"},
    	{"B-Sheet",new Integer(22),new Integer(6),"ambit2/plugin/pbt/xml/b_page.xml"},
    	{"T-Sheet",new Integer(19),new Integer(6),"ambit2/plugin/pbt/xml/t_page.xml"},
    	{"Result",new Integer(15),new Integer(5),"ambit2/plugin/pbt/xml/result_page.xml"}
    };
    public PBTWorkBook() throws Exception {
    	this(PBT_CLARIANT);
    }
    public PBTWorkBook(String file) throws Exception {
		workbook_stream = PBTMainPanel.class.getClassLoader().getResourceAsStream("ambit2/plugin/pbt/xml/"+file);
		poifsFileSystem = new POIFSFileSystem(workbook_stream);		
		workbook = new HSSFWorkbook(poifsFileSystem);
		pbt_worksheets = new PBTWorksheet[defs.length];
		for (int i=0; i < defs.length;i++)
			pbt_worksheets[i] = createSheet(workbook,i);
	}
    public String getTitle(int index) {
    	return workbook.getSheetName(index);
    }    
    public PBTWorksheet getWorksheet(int index) {
    	return pbt_worksheets[index];
    }
    public int size() {
    	return pbt_worksheets.length;
    }
    
    protected PBTWorksheet createSheet(HSSFWorkbook workbook,int index) {
		
		return new PBTWorksheet(workbook,
				defs[index][0].toString(),
				(Integer)defs[index][1],
				(Integer)defs[index][2],
				defs[index][3].toString());
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
}
