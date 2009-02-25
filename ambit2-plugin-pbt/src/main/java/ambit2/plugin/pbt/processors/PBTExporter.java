package ambit2.plugin.pbt.processors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import ambit2.core.exceptions.AmbitException;
import ambit2.core.io.FileOutputState;
import ambit2.core.processors.DefaultAmbitProcessor;
import ambit2.plugin.pbt.PBTWorkBook;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.html.HtmlWriter;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;

/**
 * Exports PBT worksheet to PDF, RTF or HTML
 * @author nina
 *
 */
public class PBTExporter extends DefaultAmbitProcessor<FileOutputState, File> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5586011097281368056L;
	protected PBTWorkBook workbook;

	public PBTExporter() {
		this(null);
	}
	public PBTExporter(PBTWorkBook workbook) {
		setWorkbook(workbook);
	}	
	public PBTWorkBook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(PBTWorkBook workbook) {
		this.workbook = workbook;
	}

	public File process(FileOutputState target) throws AmbitException {
		if (workbook == null) throw new AmbitException("Workbook not assigned!");
        Document document = new Document(PageSize.A4.rotate());
        try {
        	File file = target.getFile();
        	if (file.getName().endsWith(FileOutputState.extensions[FileOutputState.PDF_INDEX]))
        		PdfWriter.getInstance(document, new FileOutputStream(file));
        	if (file.getName().endsWith(FileOutputState.extensions[FileOutputState.RTF_INDEX]))
        		RtfWriter2.getInstance(document, new FileOutputStream(file));
        	if (file.getName().endsWith(FileOutputState.extensions[FileOutputState.HTML_INDEX]))
        		HtmlWriter.getInstance(document, new FileOutputStream(file));        		
            document.open();
            workbook.write(document);
            return file;
        }
        catch(FileNotFoundException e) {
            throw new AmbitException(e);  
        }
        catch(DocumentException e) {
            throw new AmbitException(e);
        } finally {
        	document.close();
        }
	}
}
