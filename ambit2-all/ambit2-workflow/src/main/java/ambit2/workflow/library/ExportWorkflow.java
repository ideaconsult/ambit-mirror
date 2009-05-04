package ambit2.workflow.library;

import ambit2.core.io.FileOutputState;
import ambit2.core.processors.Reporter;
import ambit2.workflow.ProcessorPerformer;

import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

/**
 * An workflow to select output file and 
 * @author nina
 *
 */
public abstract class ExportWorkflow<Content> extends Workflow {

	public ExportWorkflow()  {

        Sequence seq=new Sequence();
        seq.setName("[Export PBT assessment results]");    	

    	Primitive<Content,FileOutputState> export = new Primitive<Content,FileOutputState>( 
    			getContentTag(),
    			"FILE",
    			new Exporter<Content>(getProcessor(),getOutputTag()));
    	export.setName("Export as PDF/RTF/HTML/SDF file");
    	
    	FileOutputState fo = new FileOutputState();
    	fo.setSupportedExtensions(new String[] {".pdf",".rtf",".html",".sdf"});
    	fo.setSupportedExtDescriptions(new String[] {"Adobe PDF files (*.pdf)","Rich Text Format files (*.rtf)","HTML files (*.html)","SDF (*.sdf)"});
        setDefinition(new OutputFileSelection(export, fo));
        
	}
	
	protected abstract Reporter<Content,FileOutputState> getProcessor() ;
	protected abstract String getContentTag();
	protected abstract String getOutputTag();
	

}

class Exporter<Content> extends ProcessorPerformer<Reporter<Content,FileOutputState>,Content,FileOutputState> {
	protected String output_tag;
	public Exporter(Reporter<Content,FileOutputState> processor,String output_tag) {
		super(processor);
		this.output_tag =output_tag;
	}
	@Override
	public FileOutputState execute() throws Exception {
		Reporter<Content,FileOutputState> exporter = getProcessor();
		exporter.setOutput((FileOutputState)context.get(output_tag));
		return super.execute();
	}
}
