package ambit2.dbui;

import javax.swing.JComponent;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.search.BooleanCondition;
import ambit2.db.search.EQCondition;
import ambit2.db.search.structure.QuerySMARTS;

public class QuerySmartsEditor extends QueryEditor<String,String,BooleanCondition, IStructureRecord, QuerySMARTS> {

	public JComponent buildPanel() {

		FormLayout layout = new FormLayout(
	            "125dlu,3dlu,75dlu,3dlu,125dlu",
				"pref,pref");        
	     PanelBuilder panel = new PanelBuilder(layout);
	     panel.setDefaultDialogBorder();

	     CellConstraints cc = new CellConstraints();   

	     panel.addSeparator("SMARTS", cc.xywh(1,1,5,1));
	     panel.add(createValueComponent(), cc.xywh(1,2,5,1));	  
	     return panel.getPanel();
	}		
	@Override
	protected JComponent createConditionComponent() {
		return null;
	}

	@Override
	protected JComponent createFieldnameComponent() {
		return null;
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}
