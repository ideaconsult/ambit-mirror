package ambit2.plugin.pbt.processors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectWriter;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.io.FileOutputState;
import ambit2.core.io.MDLWriter;
import ambit2.core.processors.ProcessorFileExport;
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
public class PBTExporter extends ProcessorFileExport<PBTWorkBook> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5586011097281368056L;

	@Override
	protected void write(Document document, PBTWorkBook content)
			throws DocumentException {
		content.write(document);
		
	}
	@Override
	protected void writeHTML(Document document, OutputStream out)
			throws AmbitException {
		HtmlWriter.getInstance(document, out); 
	}
	@Override
	protected void writePDF(Document document, OutputStream out)
			throws DocumentException {
		PdfWriter.getInstance(document,out);
	}
	@Override
	protected void writeRTF(Document document, OutputStream out)
			throws AmbitException {
		RtfWriter2.getInstance(document, out);
	}
	@Override
	protected void writeSDF(PBTWorkBook content, OutputStream out)
			throws AmbitException {
	
		IAtomContainer a = PBTProperties.getAtomContainer(content);
		IChemObjectWriter writer = FileOutputState.getWriter( out,FileOutputState.extensions[FileOutputState.SDF_INDEX]);
		try {
			if (writer instanceof MDLWriter)
				((MDLWriter)writer).setSdFields(a.getProperties());
    		writer.write(a);
    		writer.close();
		} catch (Exception x) {
			logger.warn(x);
		}
	}

}
