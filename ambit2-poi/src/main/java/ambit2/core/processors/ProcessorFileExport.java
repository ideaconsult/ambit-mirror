package ambit2.core.processors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import ambit2.core.io.FileOutputState;
import ambit2.core.io.FileState._FILE_TYPE;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;

/**
 * TODO merge with representation convertor
 * make use of http://jmimemagic.sourceforge.net/maven-reports.html  for mime recognition 
 * @author nina
 *
 * @param <Content>
 */
public abstract class ProcessorFileExport<Content> extends DefaultAmbitProcessor<Content,FileOutputState>
												implements Reporter<Content,FileOutputState>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5006607500469902058L;

	protected FileOutputState output;
	protected String licenseURI;

	public String getLicenseURI() {
		return licenseURI;
	}

	public void setLicenseURI(String licenseURI) {
		this.licenseURI = licenseURI;
	}

	public FileOutputState getOutput() {
		return output;
	}

	public void setOutput(FileOutputState output) {
		this.output = output;
	}

	public ProcessorFileExport() {
		this(null);
	}
	public ProcessorFileExport(FileOutputState output) {
		setOutput(output);
	}

	public FileOutputState process(Content content) throws AmbitException {
		if (content == null) throw new AmbitException("Workbook not assigned!");
        Document document = new Document(PageSize.A4.rotate());
        try {
        	File file = getOutput().getFile();
        	if (_FILE_TYPE.SDF_INDEX.hasExtension(file)) {
        		writeSDF(content,new FileOutputStream(file));
        		return getOutput();
          	} else if (_FILE_TYPE.PDF_INDEX.hasExtension(file))
        		writePDF(document,new FileOutputStream(file));
        	else if (_FILE_TYPE.RTF_INDEX.hasExtension(file))
        		writeRTF(document,new FileOutputStream(file));
        	else if (_FILE_TYPE.HTML_INDEX.hasExtension(file))
        		writeHTML(document,new FileOutputStream(file));        	  
        	else if (_FILE_TYPE.XLS_INDEX.hasExtension(file)) {
        		writeXLS(content,new FileOutputStream(file));
        		return getOutput();        	
        	}
            document.open();
            write(document, content);
            return getOutput();
        }
        catch (DocumentException e) {
        	 throw new AmbitException(e);  
        }
        catch(FileNotFoundException e) {
            throw new AmbitException(e);  
        } finally {
        	document.close();
        }
	}	
	@Override
	public String getFileExtension() {
		return null;
	}
	protected abstract void write (Document document, Content content)throws DocumentException ;
	protected abstract void  writePDF(Document document, OutputStream out) throws DocumentException ;
	protected abstract void  writeSDF(Content content, OutputStream out) throws AmbitException ;
	protected abstract void  writeRTF(Document document, OutputStream out) throws AmbitException ;
	protected abstract void  writeHTML(Document document, OutputStream out) throws AmbitException ;
	protected abstract void  writeXLS(Content content, OutputStream out) throws AmbitException ;     
}