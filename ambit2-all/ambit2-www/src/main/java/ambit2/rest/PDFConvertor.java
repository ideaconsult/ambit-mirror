package ambit2.rest;

import java.io.IOException;
import java.io.OutputStream;

import org.restlet.data.MediaType;
import org.restlet.resource.OutputRepresentation;
import org.restlet.resource.Representation;

import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;


public class PDFConvertor<T,Q extends IQueryRetrieval<T>>  extends RepresentationConvertor<T,Q,Document> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7974532412944774457L;
	
	public PDFConvertor(QueryReporter<T, Q, Document> reporter) {
		super(reporter);
	}

	public Representation process(final Q query) throws ambit2.base.exceptions.AmbitException {
		 return new OutputRepresentation(MediaType.APPLICATION_PDF) {
	            @Override
	            public void write(OutputStream stream) throws IOException {
	            	Document document = null;   
	            	PdfWriter pdfWriter = null;
	            	try {
	            		document = new Document(PageSize.A4, 80, 50, 30, 65);
	            		pdfWriter = PdfWriter.getInstance(document, stream);
	                    //writer.setViewerPreferences(PdfWriter.HideMenubar| PdfWriter.HideToolbar);
	                    pdfWriter.setViewerPreferences(PdfWriter.PageModeUseThumbs | PdfWriter.PageModeUseOutlines);
	            		getReporter().setOutput(document);
	            		getReporter().process(query);
	            		
	            	} catch (Exception x) {
	            		Throwable ex = x;
	            		while (ex!=null) {
	            			if (ex instanceof IOException) 
	            				throw (IOException)ex;
	            			ex = ex.getCause();
	            		}
	            		x.printStackTrace();
	            	} finally {
	            		try {getReporter().close(); } catch (Exception x) { x.printStackTrace();}
	            		try {if (stream !=null) stream.flush(); } catch (Exception x) { x.printStackTrace();}
	            		pdfWriter.close();
	            	}
	            }
	        };		
	};	


}
