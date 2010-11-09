package ambit2.plugin.dbtools;

import nplugins.core.Introspection;
import ambit2.base.data.ClassHolder;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SelectionBean;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.io.FileInputState;
import ambit2.core.io.IInputState;
import ambit2.core.processors.structure.key.IStructureKey;
import ambit2.db.IDBProcessor;
import ambit2.db.processors.BatchDBProcessor;
import ambit2.db.processors.RepositoryWriter;
import ambit2.workflow.ActivityPrimitive;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.UserInteraction;
import ambit2.workflow.library.DatasetSelection;
import ambit2.workflow.library.InputFileSelection;
import ambit2.workflow.library.LoginSequence;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

public class ImportWorkflow extends Workflow {

	public ImportWorkflow() {
		SourceDataset dataset = new SourceDataset("Default");

		Sequence seq = new Sequence();
		seq.setName("[Import chemical structures]");

		final RepositoryWriter writer = new RepositoryWriter();
		writer.setDataset(dataset);
		final ProcessorsChain<String, IBatchStatistics, IProcessor> chain = new ProcessorsChain<String, IBatchStatistics, IProcessor>();
		// chain.add(processor);
		chain.add(writer);

		final BatchDBProcessor batch = new BatchDBProcessor();
		batch.setProcessorChain(chain);
		ActivityPrimitive<IInputState, IBatchStatistics> p1 = new ActivityPrimitive<IInputState, IBatchStatistics>(
				InputFileSelection.INPUTFILE, DBWorkflowContext.BATCHSTATS,
				(IDBProcessor) batch, false) {

		};
		p1.setName("Read file and import structures");
		Sequence action = getMatchKeySequence(writer);
		action.addStep(p1);

		Sequence s1 = new Sequence();
		Primitive<FileInputState, SourceDataset> p2 = new Primitive<FileInputState, SourceDataset>(
				InputFileSelection.INPUTFILE, DBWorkflowContext.DATASET,
				new Performer<FileInputState, SourceDataset>() {
					public SourceDataset execute() throws Exception {
						SourceDataset dataset = new SourceDataset(getTarget()
								.getFile().getName(), LiteratureEntry
								.getInstance(getTarget().getFilename(), "file"));
						writer.setDataset(dataset);
						return dataset;
					};
				});
		p2.setName("Create new dataset");
		s1.addStep(p2);

		s1.addStep(new DatasetSelection(action, dataset));

		setDefinition(new LoginSequence(new InputFileSelection(s1)));

	}

	public Sequence getMatchKeySequence(final RepositoryWriter writer) {
		Sequence seq = new Sequence();
		final SelectionBean<ClassHolder> selection = new SelectionBean<ClassHolder>(
				new ClassHolder[] {
						new ClassHolder(
								"ambit2.core.processors.structure.key.NoneKey",
								"None", "", ""),						
						new ClassHolder(
								"ambit2.core.processors.structure.key.CASKey",
								"CAS registry number", "", ""),
						new ClassHolder(
								"ambit2.core.processors.structure.key.EINECSKey",
								"EINECS registry number", "", ""),
						new ClassHolder(
								"ambit2.core.processors.structure.key.PubchemCID",
								"PubChem Compound ID (PUBCHEM_COMPOUND_CID)",
								"", ""),
						new ClassHolder(
								"ambit2.core.processors.structure.key.DSSToxCID",
								"SSTox Chemical ID DSSTox_CID) is a number uniquely assigned to a particular STRUCTURE across all DSSTox files",
								"", ""),
						new ClassHolder(
								"ambit2.core.processors.structure.key.DSSToxRID",
								"DSSTox Record ID (DSSTox_RID) is a number uniquely assigned to each DSSTox record across all DSSTox files",
								"", ""),
						new ClassHolder(
								"ambit2.core.processors.structure.key.InchiPropertyKey",
								"InChI", "", ""),
						new ClassHolder(
								"ambit2.core.processors.structure.key.SmilesKey",
								"SMILES", "", ""),
				// new
				// ClassHolder("ambit2.core.processors.structure.key.PropertyKey","Other property - to be defined","",""),
				}, "Match chemical compounds from file and the database by:");

		UserInteraction<SelectionBean<ClassHolder>> selectKey = new UserInteraction<SelectionBean<ClassHolder>>(
				selection, "SELECTION", "??????");
		selectKey.setName("Select how to match file and database entries");

		Performer<SelectionBean<ClassHolder>, IStructureKey> performer = new Performer<SelectionBean<ClassHolder>, IStructureKey>() {
			public IStructureKey execute() throws Exception {
				if (getTarget() instanceof IStructureKey)
					return (IStructureKey) getTarget();
				ClassHolder ch = getTarget().getSelected();
				Object o = Introspection.loadCreateObject(ch.getClazz());
				if (o instanceof IStructureKey) {
					writer.setPropertyKey((IStructureKey) o);
					return (IStructureKey) o;
				} else
					throw new Exception(o.getClass().getName()
							+ " not expected");
			}
		};

		Primitive<SelectionBean<ClassHolder>, IStructureKey> match = new Primitive<SelectionBean<ClassHolder>, IStructureKey>(
				"SELECTION", "KEY", performer) {
			@Override
			public synchronized String getName() {
				return selection.getTitle();
			}
		};
		seq.addStep(selectKey);
		seq.addStep(match);
		return seq;
	}
	@Override
	public String toString() {
		return "Import chemical structures";
	}

}
