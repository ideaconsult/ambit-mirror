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

/**
 * Writes query results as CML 
 * @author nina
 *
 * @param <Q>
 */
public class CMLReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryReporter<IStructureRecord, Q, Writer> {
	protected MoleculeReader reader = new MoleculeReader();
	/**
	 * 
	 */
	private static final long serialVersionUID = 2931123688036795689L;
	public CMLReporter() {
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
	public Writer getOutput() throws AmbitException {
		return super.getOutput();
	}
	@Override
	public void processItem(IStructureRecord item, Writer output) {
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

}