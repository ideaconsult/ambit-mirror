package ambit2.plugin.pbt;

import ambit2.core.processors.ProcessorFileExport;
import ambit2.plugin.pbt.processors.PBTExporter;
import ambit2.workflow.library.OutputFileSelection;

/**
 * An workflow to select output file and 
 * @author nina
 *
 */
public class ExportWorkflow extends ambit2.workflow.library.ExportWorkflow<PBTWorkBook> {

	public ExportWorkflow()  {
		super();
	}
	@Override
	protected String getContentTag() {
		return PBTWorkBook.PBT_WORKBOOK;
	}
	@Override
	protected ProcessorFileExport<PBTWorkBook> getProcessor() {
		return new PBTExporter();
	}
	@Override
	protected String getOutputTag() {
		return OutputFileSelection.OUTPUTFILE;
	}
	
}
