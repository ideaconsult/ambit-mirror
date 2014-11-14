package ambit2.db.reporters;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.processors.ProcessorStructureRetrieval;

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
			logger.log(java.util.logging.Level.SEVERE,x.getMessage(),x);
		}
		return null;
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	public void footer(Document output, Q query) {};
	public void header(Document output, Q query) {};
	@Override
	public String getFileExtension() {
		return "rtf";
	}
}