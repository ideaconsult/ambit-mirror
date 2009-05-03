package ambit2.core.processors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.io.FileOutputState;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;

public abstract class ProcessorFileExport<Content> extends DefaultAmbitProcessor<Content,FileOutputState>
												implements Reporter<Content,FileOutputState>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5006607500469902058L;

	protected FileOutputState output;


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
        	if (file.getName().endsWith(FileOutputState.extensions[FileOutputState.SDF_INDEX])) {
        		writeSDF(content,new FileOutputStream(file));
        		return getOutput();
          	} else if (file.getName().endsWith(FileOutputState.extensions[FileOutputState.PDF_INDEX]))
        		writePDF(document,new FileOutputStream(file));
        	else if (file.getName().endsWith(FileOutputState.extensions[FileOutputState.RTF_INDEX]))
        		writeRTF(document,new FileOutputStream(file));
        	else if (file.getName().endsWith(FileOutputState.extensions[FileOutputState.HTML_INDEX]))
        		writeHTML(document,new FileOutputStream(file));        	  
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
	
	protected abstract void write (Document document, Content content)throws DocumentException ;
	protected abstract void  writePDF(Document document, OutputStream out) throws DocumentException ;
	protected abstract void  writeSDF(Content content, OutputStream out) throws AmbitException ;
	protected abstract void  writeRTF(Document document, OutputStream out) throws AmbitException ;
	protected abstract void  writeHTML(Document document, OutputStream out) throws AmbitException ;        	
}