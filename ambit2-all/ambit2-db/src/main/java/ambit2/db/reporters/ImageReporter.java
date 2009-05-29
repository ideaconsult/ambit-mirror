package ambit2.db.reporters;

import java.io.OutputStream;

import javax.imageio.ImageIO;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.io.CompoundImageTools;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;

public class ImageReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryReporter<IStructureRecord, Q, OutputStream> {
	protected CompoundImageTools depict = new CompoundImageTools();
	protected MoleculeReader reader = new MoleculeReader();
	/**
	 * 
	 */
	private static final long serialVersionUID = 2931123688036795689L;
	public ImageReporter() {
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
	public OutputStream getOutput() throws AmbitException {
		return super.getOutput();
	}
	@Override
	protected void processItem(IStructureRecord item, OutputStream output) {
		try {
			ImageIO.write(depict.getImage(reader.process(item)),"png",output);
			
		} catch (Exception x) {
			logger.error(x);
		}
		
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}