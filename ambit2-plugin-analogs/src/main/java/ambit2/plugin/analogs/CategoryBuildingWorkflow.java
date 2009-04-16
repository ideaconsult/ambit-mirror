package ambit2.plugin.analogs;

import ambit2.workflow.library.DefineStructure;
import ambit2.workflow.library.LoginSequence;

import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

/**
 * Category building workflow:
1.Define structure(s)	
2.Endpoint(s) selection	
3.Define profile (what properties are of interest)	
4.Load data for the target substance	
5.Calculate properties	
6.Find similar substances [Hypothesis-based
(e.g. fragments, smarts, property range, % from target) ]	
7.Load data for the similar substances	
8.Analysis	
[Calculate measure of homogeneity;
Remove substances that does not fit in the category – manually or by comparing to a threshold
Fill datagaps by read across

member of the category, mutual similarity 1-2 , 1-3, 2-3; this is basicaly pairwise similarity; 
perhaps a statistics over the pairwise similarity matrix - median, dispersion; look for singletons, etc.
]
9.Report

 
 * @author nina
 *
 */
public class CategoryBuildingWorkflow extends Workflow {
	public CategoryBuildingWorkflow() {
    	
		
        Sequence seq=new Sequence();
        seq.setName("Category building");    	
        
        
        seq.addStep(new DefineStructure());
        //seq.addStep(new QuerySelection().addStep(new QueryExecution(new QueryField())));        
        /*
        seq.addStep(new EndpointSelection());
        
        seq.addStep(new DefineProfile());
        seq.addStep(new RetrieveProfileData());
        seq.addStep(new CalculateProfileProperties());
        */
        seq.addStep(new FindSimilarSubstances());
        /*
        seq.addStep(new ProfileAnalysis());
        seq.addStep(new GenerateReport());
       */
       setDefinition(new LoginSequence(seq));
       // setDefinition(seq);
	}
	@Override
	public String toString() {
		return "Category building";
	}
}
