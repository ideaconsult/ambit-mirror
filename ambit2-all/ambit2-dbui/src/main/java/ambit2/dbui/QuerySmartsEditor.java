package ambit2.dbui;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.search.BooleanCondition;
import ambit2.db.search.structure.QuerySMARTS;
import ambit2.descriptors.FuncGroupsDescriptorFactory;
import ambit2.descriptors.FunctionalGroup;
import ambit2.descriptors.VerboseDescriptorResult;
import ambit2.ui.Utils;
import ambit2.ui.editors.Panel2D;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * An editor for {@link QuerySMARTS}
 * @author nina
 *
 */
public class QuerySmartsEditor extends QueryEditor<String,FunctionalGroup,BooleanCondition, IStructureRecord, QuerySMARTS> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9201007230031542807L;
	public JComponent buildPanel() {

		FormLayout layout = new FormLayout(
	            "125dlu,3dlu,75dlu,3dlu,125dlu",
				"pref,pref,pref,pref,pref,pref,pref,pref,pref,pref:grow");        
	     PanelBuilder panel = new PanelBuilder(layout);
	     panel.setDefaultDialogBorder();

	     CellConstraints cc = new CellConstraints();   

			FuncGroupsDescriptorFactory factory = new FuncGroupsDescriptorFactory();
			List<FunctionalGroup> gf;
			try {
				gf = factory.process("ambit2/descriptors/strucfeatures.xml");
			} catch (Exception x) {gf = new ArrayList<FunctionalGroup>();}
			final SelectionInList<FunctionalGroup> selectionInList = new SelectionInList<FunctionalGroup>(
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
	        	panel.add(t,cc.xywh(3,j+2,3,1));	   
	        }
	        
	        final Panel2D panel2D = new Panel2D();
	 	    panel2D.setToolTipText("Double click here to edit structure");
	 	    panel2D.setPreferredSize(new Dimension(200,200));
	 	    panel2D.setMinimumSize(new Dimension(150,150));
	        panel2D.setBorder(BorderFactory.createTitledBorder("Test compound"));
	        
	       
	       
	          
	        final JLabel status = new JLabel("");
	        status.setBorder(BorderFactory.createTitledBorder("Test result"));
	    	JButton b = new JButton(new AbstractAction("Test SMARTS") {
				public void actionPerformed(java.awt.event.ActionEvent arg0) {
					try {
						if (selectionInList.getSelection()==null) {
							status.setText("No SMARTS selected!");
							status.setIcon(null);
						} else if (panel2D.getObject()==null) {
							status.setText("Empty test structure!");
							status.setIcon(null);
						} else {
							selectionInList.getSelection().setVerboseMatch(true);
							VerboseDescriptorResult result = selectionInList.getSelection().process(panel2D.getObject());
							
							if (result.getExplanation() instanceof String) {
								status.setText(result.getExplanation().toString());
								status.setIcon(null);
							} else {
								boolean notfound = result.getResult().toString().equals("0");
								status.setText((notfound)?"NOT FOUND":"FOUND");
								status.setIcon((notfound)?Utils.createImageIcon("images/cross.png"):Utils.createImageIcon("images/tick.png"));
								panel2D.setSelected((notfound)?null:(IAtomContainer)result.getExplanation());
							}
						}
					} catch (Exception x) {
						status.setText(x.getMessage());
						status.setIcon(null);
					} finally {
						if (selectionInList.getSelection()!=null)
							selectionInList.getSelection().setVerboseMatch(false);
					}
					
				};
			});
			b.setMinimumSize(new Dimension(64,18));
			b.setToolTipText("Click here to verify SMARTS validity");	  
			
			panel.addSeparator("Test Structure",cc.xywh(3,config.length+2,3,1));
			panel.add(panel2D,cc.xywh(3,config.length+3,3,4));
			//panel.add(new JLabel("Test structure"),cc.xywh(1,config.length+3,1,1));

			panel.add(b,cc.xywh(1,config.length+2,1,1));
			panel.add(status,cc.xywh(1,config.length+5,1,2));
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
