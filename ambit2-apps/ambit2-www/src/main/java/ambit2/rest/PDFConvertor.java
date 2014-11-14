package ambit2.rest;

import java.io.IOException;
import java.io.OutputStream;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.c.RepresentationConvertor;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Converts query results to PDF
 * @author nina
 *
 * @param <T>
 * @param <Q>
 * @param <R>
 */
public class PDFConvertor<T,Q extends IQueryRetrieval<T>,R extends Reporter<Q,Document>>  extends 
				RepresentationConvertor<T,Q,Document,R> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7974532412944774457L;
	
	public PDFConvertor(R reporter) {
		super(reporter);
	}
	public PDFConvertor(R reporter,String fileNamePrefix) {
		super(reporter,MediaType.APPLICATION_PDF,fileNamePrefix);
	}
	public Representation process(final Q query) throws AmbitException {
		Representation rep = new OutputRepresentation(MediaType.APPLICATION_PDF) {
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
            			Context.getCurrentLogger().severe(x.getMessage());

	            	} finally {
	            		try {getReporter().close(); } catch (Exception x) { }
	            		try {if (stream !=null) stream.flush(); } catch (Exception x) { }
	            		pdfWriter.close();
	            	}
	            }
	        };		
	        setDisposition(rep);
	        return rep;
	};	


}
