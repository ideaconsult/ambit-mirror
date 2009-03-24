package ambit2.plugin.usermgr;

import ambit2.base.data.StringBean;
import ambit2.db.processors.DbCreateDatabase;
import ambit2.workflow.ActivityPrimitive;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.UserInteraction;
import ambit2.workflow.library.LoginSequence;

import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

public class CreateDatabaseWorkflow extends Workflow {
	public static String NEWDATABASE = "ambit2.plugin.dbtools.NEWDATABASE";
	public CreateDatabaseWorkflow() {
        Sequence seq=new Sequence();
        seq.setName("[Create database]");    	

    	ActivityPrimitive<StringBean,String> p1 = new ActivityPrimitive<StringBean,String>( 
    			NEWDATABASE,
    		    DBWorkflowContext.STOREDQUERY,
    			new DbCreateDatabase(),true);
    	
    	
	
        p1.setName("Create database");    

        seq.addStep(new UserInteraction<StringBean>(
        		new StringBean("ambit-new"),NEWDATABASE,"Specify database name"));
        seq.addStep(p1);
        setDefinition(new LoginSequence(seq));
	}
}
