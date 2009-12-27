package ambit2.db.reporters;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;

public class RTFReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryStructureReporter< Q, Document> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2931123688036795689L;
	public RTFReporter() {
		getProcessors().clear();
		getProcessors().add(new ProcessorStructureRetrieval());
		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target);
				return target;
			};
		});	
	}

	
	@Override
	public Object processItem(IStructureRecord item) throws AmbitException {

		try {
			//TODO generate PDFcontent
			getOutput().add(new Paragraph(item.getContent()));			
		} catch (Exception x) {
			logger.error(x);
		}
		return null;
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	public void footer(Document output, Q query) {};
	public void header(Document output, Q query) {};
}