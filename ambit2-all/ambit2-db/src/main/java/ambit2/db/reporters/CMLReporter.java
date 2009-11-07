package ambit2.db.reporters;

import java.io.Writer;

import org.openscience.cdk.io.CMLWriter;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveStructure;

/**
 * Writes query results as CML 
 * @author nina
 *
 * @param <Q>
 */
public class CMLReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryStructureReporter< Q, Writer> {
	protected MoleculeReader reader = new MoleculeReader();
	/**
	 * 
	 */
	private static final long serialVersionUID = 2931123688036795689L;
	public CMLReporter() {
		getProcessors().clear();
		RetrieveStructure r = new RetrieveStructure();
		r.setMaxRecords(1);
		getProcessors().add(new ProcessorStructureRetrieval(r));		
		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target);
				return target;
			};
		});	
	}

	@Override
	public Writer getOutput() throws AmbitException {
		return super.getOutput();
	}
	@Override
	public void processItem(IStructureRecord item) throws AmbitException {
		try {
			CMLWriter cmlwriter = new CMLWriter(output);
			cmlwriter.write(reader.process(item));
			cmlwriter.close();
			
		} catch (Exception x) {
			logger.error(x);
		}
		
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	public void footer(Writer output, Q query) {};
	public void header(Writer output, Q query) {};

}