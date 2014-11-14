package ambit2.db.processors;

import java.io.File;

import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.core.processors.structure.StructureTypeProcessor;

/**
 * @author nina
 *
 */
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
			//protected transient SMARTSPropertiesGenerator atomprops = new SMARTSPropertiesGenerator();
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
		/*
		 * 		TODO - generate atomproperties on import 
	batch.getProcessorChain().add(new SMARTSPropertiesGenerator());
	//batch.getProcessorChain().add(new SMARTSAcceleratorWriter());
		 */
		
		SourceDataset dataset = new SourceDataset(file.getName(),
				LiteratureEntry.getInstance("File", file.getName()));
		processor.add(new DbStructureWriter(dataset));
		setProcessorChain(processor);
	}

}
