package ambit2.dbui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.search.BooleanCondition;
import ambit2.db.search.structure.QuerySMARTS;
import ambit2.descriptors.FuncGroupsDescriptorFactory;
import ambit2.descriptors.FunctionalGroup;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class QuerySmartsEditor extends QueryEditor<String,FunctionalGroup,BooleanCondition, IStructureRecord, QuerySMARTS> {

	public JComponent buildPanel() {

		FormLayout layout = new FormLayout(
	            "125dlu,3dlu,75dlu,3dlu,125dlu",
				"pref,pref,pref,pref,pref");        
	     PanelBuilder panel = new PanelBuilder(layout);
	     panel.setDefaultDialogBorder();

	     CellConstraints cc = new CellConstraints();   

			FuncGroupsDescriptorFactory factory = new FuncGroupsDescriptorFactory();
			List<FunctionalGroup> gf;
			try {
				gf = factory.process("ambit2/descriptors/strucfeatures.xml");
			} catch (Exception x) {gf = new ArrayList<FunctionalGroup>();}
			SelectionInList<FunctionalGroup> selectionInList = new SelectionInList<FunctionalGroup>(
            		gf,
            		presentationModel.getModel("value"));
			JComboBox box = BasicComponentFactory.createComboBox(selectionInList);
			panel.add(box,cc.xywh(1,1,5,1));
			
	        String[][] config = {
	        		{"family","Group"},        		
	        		{"name","Name"},
	        		{"smarts","SMARTS"},
	        		{"hint","Description"},
	        };
	        int offset=2;
	        BeanAdapter beanAdapter = new BeanAdapter(selectionInList);
	        for (int j=0; j < config.length;j++) {
	        	String[] c = config[j];
	        	ValueModel model = beanAdapter.getValueModel(c[0]);
	        	panel.add(BasicComponentFactory.createLabel(new ValueHolder(c[1])),cc.xywh(1,j+2,1,1));
	        	JTextField t = BasicComponentFactory.createTextField(model);
	        	t.addMouseListener(new MouseAdapter() {
	        		@Override
	        		public void mouseEntered(MouseEvent e) {
	        			super.mouseEntered(e);
	        			((JTextField)e.getSource()).setToolTipText(((JTextField)e.getSource()).getText());
	        		}
	        	});
	        	panel.add(t,cc.xywh(2,j+2,4,1));	   
	        }
	          
	     return panel.getPanel();
	}		

	@Override
	protected JComponent createConditionComponent() {
		return null;
	}
	protected static final String t_group = "group";
	protected static final String t_name = "name";
	protected static final String t_smarts = "smarts";
	protected static final String t_hint = "hint";
	protected static final String t_family = "family";
	@Override
	protected JComponent createFieldnameComponent() {
		return null;
	}


	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}
