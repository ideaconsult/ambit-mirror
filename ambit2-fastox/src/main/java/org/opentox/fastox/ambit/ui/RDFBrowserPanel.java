package org.opentox.fastox.ambit.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;

import org.opentox.fastox.ambit.workflow.RDFWorkflowContext;

import ambit2.base.exceptions.AmbitException;
import ambit2.ui.table.IBrowserMode.BrowserMode;
import ambit2.workflow.ui.AbstractStructureBrowserPanel;

import com.hp.hpl.jena.ontology.OntModel;
import com.microworkflow.process.WorkflowContext;

public class RDFBrowserPanel extends AbstractStructureBrowserPanel<OntModel, RDFTableModel> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7242773868707958520L;

	public RDFBrowserPanel(WorkflowContext wfcontext,BrowserMode mode) {
        super(wfcontext,BorderLayout.NORTH,mode);
    }

	@Override
	protected RDFTableModel createTableModel() {
		return new RDFStructuresTableModel();
	}

	@Override
	protected OntModel getObject() {
		return tableModel.getRecords();
	}

	@Override
	protected void processRecord(int record) {
	}

	@Override
	protected void setQuery(OntModel object) throws AmbitException {
		tableModel.setRecords(object);
	}

	@Override
	protected void animate(PropertyChangeEvent arg0) {
		  if (RDFWorkflowContext.WFC_KEY.ONTMODEL.toString().equals(arg0.getPropertyName())) 
              try {
            	  Object o = arg0.getNewValue();
                  setQuery((OntModel)o);
              } catch (Exception x) {
                  x.printStackTrace();
              }
		
	}
}
