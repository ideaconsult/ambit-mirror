package ambit2.rest.dataset;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.db.SourceDataset;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.rest.query.XMLTags;


public class DatasetURIReporter<Q extends IQueryRetrieval<SourceDataset>> extends QueryReporter<SourceDataset, Q, Writer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3648376868814044783L;
	protected Reference baseReference;
	public DatasetURIReporter(Reference baseRef) {
		this.baseReference = baseRef;
	}
	public DatasetURIReporter() {
	}	
	@Override
	public void processItem(SourceDataset dataset, Writer output) {
		try {
			String ref = baseReference.toString();
			if (ref.endsWith("/")) ref = ref.substring(0,ref.length()-1);			
			output.write(String.format("%s/%s/%s", 
					ref,
					XMLTags.node_dataset,dataset==null?"":Reference.encode(dataset.getName()) )
					);
			output.flush();
		} catch (IOException x) {
			x.printStackTrace();
		}
		
	}
	public void footer(Writer output, Q query) {};
	public void header(Writer output, Q query) {};
	
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
}	