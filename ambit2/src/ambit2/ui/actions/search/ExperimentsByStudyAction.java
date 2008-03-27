package ambit2.ui.actions.search;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JFrame;

import ambit2.database.data.AmbitDatabaseToolsData;
import ambit2.database.processors.ExperimentSearchProcessor;
import ambit2.ui.actions.AmbitAction;

/**
 * {@link ambit2.data.experiment.Study} query. See example for {@link ambit2.database.data.AmbitDatabaseToolsData}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class ExperimentsByStudyAction extends AmbitAction {

	public ExperimentsByStudyAction(Object userData, JFrame mainFrame) {
		super(userData, mainFrame);
		// TODO Auto-generated constructor stub
	}

	public ExperimentsByStudyAction(Object userData, JFrame mainFrame,
			String arg0) {
		super(userData, mainFrame, arg0);
		// TODO Auto-generated constructor stub
	}

	public ExperimentsByStudyAction(Object userData, JFrame mainFrame,
			String arg0, Icon arg1) {
		super(userData, mainFrame, arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	public void run(ActionEvent arg0) {
		super.run(arg0);
		try {
			if (userData instanceof AmbitDatabaseToolsData) {
				AmbitDatabaseToolsData dbaData = ((AmbitDatabaseToolsData) userData);
				ExperimentSearchProcessor p = new ExperimentSearchProcessor(dbaData.getDbConnection().getConn());
				dbaData.getExperiments().clear();
				dbaData.getStudyConditions().clear();
				p.loadQuery(dbaData.getExperiments().getStudy(),dbaData.getExperiments(),true);
				p.loadQuery(dbaData.getExperiments().getStudy(),dbaData.getStudyConditions(),false);
				p.close();
				p = null;
			}	
			
		} catch (Exception x) {
			
		}
		
	}

}
