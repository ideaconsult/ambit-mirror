package org.opentox.fastox.ambit.ui;

import org.opentox.rdf.OT;
import org.opentox.rdf.OT.OTClass;

import ambit2.rest.rdf.RDFObjectIterator;
import ambit2.ui.table.IBrowserMode.BrowserMode;

import com.microworkflow.process.WorkflowContext;

/**
 * Browses {@link OTClass#Model}
 * @author nina
 *
 */
public class RDFModelsBrowserPanel extends RDFBrowserPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2504240801684894101L;
	
	public RDFModelsBrowserPanel(WorkflowContext wfcontext, BrowserMode mode) {
		super(wfcontext, mode);
	}

	@Override
	protected RDFTableModel createTableModel() {
		return new RDFTableModel(OT.OTClass.Model) {
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				switch (columnIndex) {
				case 0: {
					try { return RDFObjectIterator.getTitle(resources.get(rowIndex));}
					catch (Exception x) { return super.getValueAt(rowIndex, columnIndex);}
				}
				/*
				case 1: {
					try { return RDFObjectIterator.getIdentifier(resources.get(rowIndex));}
					catch (Exception x) { return super.getValueAt(rowIndex, columnIndex);}
					
				}
				*/
				default: return super.getValueAt(rowIndex, columnIndex);
				}
				
			}
		};
	}

}
