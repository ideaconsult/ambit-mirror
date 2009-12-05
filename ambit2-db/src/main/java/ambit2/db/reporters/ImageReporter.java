package ambit2.db.reporters;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.io.CompoundImageTools;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;

public class ImageReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryReporter<IStructureRecord, Q,BufferedImage > {
	protected CompoundImageTools depict = new CompoundImageTools();
	protected MoleculeReader reader = new MoleculeReader();
	/**
	 * 
	 */
	private static final long serialVersionUID = 2931123688036795689L;
	
	public ImageReporter() {
		this(new Dimension(250,250));
	}
	public ImageReporter(Dimension dimension) {
		super();
		depict.setImageSize(dimension);
		getProcessors().clear();
		getProcessors().add(new ProcessorStructureRetrieval());
		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target);
				return target;
			};
		});	
		setMaxRecords(1);
	}


	@Override
	public void processItem(IStructureRecord item) throws AmbitException {
		try {
			setOutput(depict.getImage(reader.process(item)));
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		
	}
	
	public void open() throws DbAmbitException {
	}
	@Override
	public void footer(BufferedImage output, Q query) {
		
	}
	@Override
	public void header(BufferedImage output, Q query) {
	}

}