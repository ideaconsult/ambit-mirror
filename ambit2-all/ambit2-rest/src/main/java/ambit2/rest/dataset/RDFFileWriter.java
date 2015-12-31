package ambit2.rest.dataset;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.AbstractDBProcessor;

import org.codehaus.stax2.XMLOutputFactory2;
import org.restlet.data.Reference;

import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;

public class RDFFileWriter extends
		AbstractDBProcessor<IStructureRecord, IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7626753873168651075L;
	protected DatasetRDFWriter recordWriter;

	public RDFFileWriter(File file, Reference appReference, Template template)
			throws Exception {
		super();
		recordWriter = new DatasetRDFWriter(appReference, null);
		recordWriter.setTemplate(template == null ? new Template() : template);
		XMLStreamWriter writer = null;
		try {
			XMLOutputFactory factory = XMLOutputFactory2.newInstance();
			writer = factory.createXMLStreamWriter(new FileOutputStream(file),
					"UTF-8");
			recordWriter.setOutput(writer);
			recordWriter.header(writer);

		} catch (Exception x) {
			throw new IOException(x.getMessage());
		} finally {

		}
	}

	@Override
	public void close() {
		try {
			recordWriter.footer(recordWriter.getOutput());

			recordWriter.close();
		} catch (Exception x) {
		}
	}

	@Override
	public IStructureRecord process(IStructureRecord target)
			throws AmbitException {
		for (Property p : target.getRecordProperties())
			recordWriter.getTemplate().add(p);

		return recordWriter.process(target);
	}

	@Override
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub

	}

}