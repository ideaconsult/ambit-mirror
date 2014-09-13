package ambit2.db.processors;

import java.io.File;
import java.util.logging.Level;

import org.openscience.cdk.interfaces.IAtomContainer;

import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.batch.IBatchStatistics.RECORDS_STATS;
import net.idea.modbcum.i.processors.IProcessor;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.core.processors.structure.StructureTypeProcessor;

public class QuickImportBatchProcessor extends BatchDBProcessor<IStructureRecord> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4345359507645947184L;
	public QuickImportBatchProcessor(File file) {
		super();
		
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> processor = new ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor>();

		processor.add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			protected transient MoleculeReader reader = new MoleculeReader();
			protected transient StructureTypeProcessor strucType = new StructureTypeProcessor();
			@Override
			public IStructureRecord process(IStructureRecord record)
					throws Exception {
				try {
					IAtomContainer molecule = reader.process(record);
					/*
					if ((molecule != null) && (molecule.getProperties()!=null))
						record.addProperties(molecule.getProperties());
					*/
					record.setType(strucType.process(molecule));					
					return record;
				} catch (Exception x) {
					record.setType(STRUC_TYPE.NA);
					return record;
				}
			}
		});
		SourceDataset dataset = new SourceDataset(file.getName(),
				LiteratureEntry.getInstance("File", file.getName()));
		processor.add(new DbStructureWriter(dataset));
		setProcessorChain(processor);
	}

}
