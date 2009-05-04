package ambit2.db.reporters;

import java.io.Writer;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;

public class PDFReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryReporter<IStructureRecord, Q, Document> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2931123688036795689L;
	public PDFReporter() {
		getProcessors().clear();
		getProcessors().add(new ProcessorStructureRetrieval());
		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target,getOutput());
				return target;
			};
		});	
	}

	
	@Override
	protected void processItem(IStructureRecord item, Document document) {

		try {
			//TODO generate PDFcontent
			document.add(new Paragraph(item.getContent()));			
		} catch (Exception x) {
			logger.error(x);
		}
		
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}