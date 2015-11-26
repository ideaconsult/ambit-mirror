package ambit2.db.reporters.xlsx;

import java.io.OutputStream;

import net.idea.modbcum.i.IQueryRetrieval;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ambit2.base.data.Profile;
import ambit2.base.data.Template;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.reporters.QueryHeaderReporter;

public abstract class AbstractXLSXReporter<Q extends IQueryRetrieval<IStructureRecord>>
		extends QueryHeaderReporter<Q, OutputStream> {

	protected Workbook workbook;
	protected Sheet sheet;
	protected DataFormat dataformat;
	protected CellStyle style;
	protected OutputStream out;
	protected boolean writingStarted = false;
	/**
	 * 
	 */
	private static final long serialVersionUID = -6627313415375827936L;

	public AbstractXLSXReporter(String baseRef,boolean hssf, Template template, Profile groupedProperties,
		    SubstanceEndpointsBundle[] bundles, String urlPrefix, boolean includeMol) {
		super();
		
		this.includeMol = includeMol;
		//setUrlPrefix(urlPrefix);
		setGroupProperties(groupedProperties);
		setTemplate(template == null ? new Template(null) : template);
		//this.folders = folders;
		//this.bundles = bundles;
		getProcessors().clear();
		configureProcessors(baseRef, includeMol);
		
		workbook = hssf ? new HSSFWorkbook() : new XSSFWorkbook();
		sheet = workbook.createSheet();
		dataformat = workbook.createDataFormat();
		style = workbook.createCellStyle();
		style.setDataFormat(dataformat.getFormat("0.00"));
	}
	
	@Override
	public void footer(OutputStream output, Q query) {
		try {
			workbook.write(output);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}	
	
	protected RichTextString createRichTextString(String value, Font font) {
		RichTextString rts;
		if (sheet instanceof HSSFSheet) {
			rts = new HSSFRichTextString(value);
		} else {
			rts = new XSSFRichTextString(value);
		}
		rts.applyFont(font);
		return rts;
	}
}
