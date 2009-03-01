package ambit2.plugin.pbt;

import java.io.File;

import ambit2.core.io.FileOutputState;
import ambit2.plugin.pbt.processors.PBTExporter;
import ambit2.workflow.ProcessorPerformer;
import ambit2.workflow.library.OutputFileSelection;

import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

/**
 * An workflow to select output file and 
 * @author nina
 *
 */
public class ExportWorkflow extends Workflow {

	public ExportWorkflow()  {
		

        Sequence seq=new Sequence();
        seq.setName("[Export PBT assessment results]");    	

    	Primitive<FileOutputState,File> export = new Primitive<FileOutputState,File>( 
    			OutputFileSelection.OUTPUTFILE,
    			"FILE",
    			new Exporter(new PBTExporter()));
    	export.setName("Export as PDF/RTF/HTML/SDF file");
    	
    	FileOutputState fo = new FileOutputState();
    	fo.setSupportedExtensions(new String[] {".pdf",".rtf",".html",".sdf"});
    	fo.setSupportedExtDescriptions(new String[] {"Adobe PDF files (*.pdf)","Rich Text Format files (*.rtf)","HTML files (*.html)","SDF (*.sdf)"});
        setDefinition(new OutputFileSelection(export, fo));
	}

}

class Exporter extends ProcessorPerformer<PBTExporter, FileOutputState,File> {
	public Exporter(PBTExporter processor) {
		super(processor);
	}
	@Override
	public File execute() throws Exception {
		PBTExporter pbtExporter = getProcessor();
		Object workbook = context.get(PBTWorkBook.PBT_WORKBOOK);
		if ((workbook != null) && (workbook instanceof PBTWorkBook))
			pbtExporter.setWorkbook((PBTWorkBook) workbook);
		return super.execute();
	}
}