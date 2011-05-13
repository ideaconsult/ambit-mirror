package ambit2.dbui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IAmbitEditor;
import ambit2.db.search.QueryInfo;
import ambit2.db.search.StringCondition.STRING_CONDITION;
import ambit2.ui.Panel2D;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class QueryInfoEditor extends JPanel implements IAmbitEditor<QueryInfo> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4337363916900586486L;
	protected QueryInfo object;
	protected QueryInfoModel detailsModel;
	
	protected JTextField tf_identifier1;
	protected JTextField tf_identifier2;
	protected JTextField tf_identifier3;

	protected JTextField tf_formula;
	protected JTextField tf_smiles;
	protected JTextField tf_inchi;

	protected JComboBox threshold;
	
	protected JComboBox dataset;
	protected JComboBox fieldnames1;
	protected JComboBox fieldnames2;
	protected JComboBox fieldnames3;
	
	protected JComboBox method;
	protected JComboBox scope;
	
	protected JComboBox cond1;
	protected JComboBox cond2;
	protected JComboBox cond3;
	
	protected JComboBox and;
	
	protected Panel2D panel2d;
	
	/* UNCOMMENT TO USE JCHEMPAIN
	protected MoleculeEditAction editAction;
	*/
	
	public QueryInfoEditor() {
		add(buildPanel());
		/* UNCOMMENT TO USE JCHEMPAIN
		editAction = new MoleculeEditAction(null);
		*/		
	}
	public JComponent buildPanel() {

		detailsModel = new QueryInfoModel(new ValueHolder(null, true));
		tf_identifier1 = BasicComponentFactory.createTextField(detailsModel.getModel("identifier1"),true);
		tf_identifier2 = BasicComponentFactory.createTextField(detailsModel.getModel("identifier2"),true);
		tf_identifier3 = BasicComponentFactory.createTextField(detailsModel.getModel("identifier3"),true);

		tf_formula = BasicComponentFactory.createTextField(detailsModel.getModel("formula"),true);
		tf_smiles = BasicComponentFactory.createTextField(detailsModel.getModel("smiles"),true);
		tf_inchi = BasicComponentFactory.createTextField(detailsModel.getModel("inchi"),true);
		
		
        ValueModel objectChoiceModel = detailsModel.getModel(QueryInfo.PROPERTY_SCOPE);
        scope = BasicComponentFactory.createComboBox(
                new SelectionInList<Object>(QueryInfo.scope_options, objectChoiceModel));
        
        objectChoiceModel.addValueChangeListener(new PropertyChangeListener() {
        	public void propertyChange(PropertyChangeEvent evt) {
        		
                firePropertyChange(
                        QueryInfo.PROPERTYNAME_DATASET_ENABLED,
                        null,
                        Boolean.valueOf(detailsModel.isScope(QueryInfo.SCOPE_DATASET)));
        	}
        });        
        
   
        ValueModel methodChoiceModel = detailsModel.getModel(QueryInfo.PROPERTY_METHOD);
        method = BasicComponentFactory.createComboBox(
                new SelectionInList<Object>(QueryInfo.method_options, methodChoiceModel));

        ValueModel cond1ChoiceModel = detailsModel.getModel(QueryInfo.PROPERTY_COND1);
        cond1 = BasicComponentFactory.createComboBox(
                new SelectionInList<STRING_CONDITION>(STRING_CONDITION.values(), cond1ChoiceModel));
        ValueModel cond2ChoiceModel = detailsModel.getModel(QueryInfo.PROPERTY_COND2);
        cond2 = BasicComponentFactory.createComboBox(
                new SelectionInList<STRING_CONDITION>(STRING_CONDITION.values(), cond2ChoiceModel));
        ValueModel cond3ChoiceModel = detailsModel.getModel(QueryInfo.PROPERTY_COND3);
        cond3 = BasicComponentFactory.createComboBox(
                new SelectionInList<STRING_CONDITION>(STRING_CONDITION.values(), cond3ChoiceModel));
        
        ValueModel thresholdModel = detailsModel.getModel(QueryInfo.PROPERTY_THRESHOLD);
        threshold = BasicComponentFactory.createComboBox(
                new SelectionInList<Object>(QueryInfo.threshold_options, thresholdModel));
        
                
        ValueModel datasetModel = detailsModel.getModel(QueryInfo.PROPERTY_DATASET);
        dataset = BasicComponentFactory.createComboBox(new SelectionInList<Object>(QueryInfo.getDatasets(),datasetModel));
        /*
        PropertyConnector.connect(
        		dataset, "enabled",
        		detailsModel, QueryInfo.PROPERTYNAME_DATASET_ENABLED);

        PropertyChangeListener listener = new PropertyChangeListener() {
        	public void propertyChange(PropertyChangeEvent evt) {
        		System.out.println(evt);
        	};
        };
        detailsModel.addBeanPropertyChangeListener(listener);
        */        
        
        ValueModel field1Model = detailsModel.getModel(QueryInfo.PROPERTY_FIELDNAME1);
        fieldnames1 = BasicComponentFactory.createComboBox(new SelectionInList(QueryInfo.getFieldnames(),field1Model));
        
        ValueModel field2Model = detailsModel.getModel(QueryInfo.PROPERTY_FIELDNAME2);        
        fieldnames2 = BasicComponentFactory.createComboBox(new SelectionInList(QueryInfo.getFieldnames(),field2Model));

        ValueModel field3Model = detailsModel.getModel(QueryInfo.PROPERTY_FIELDNAME3);        
        fieldnames3 = BasicComponentFactory.createComboBox(new SelectionInList(QueryInfo.getFieldnames(),field3Model));
        
        ValueModel booleanChoiceModel = detailsModel.getModel(QueryInfo.PROPERTY_COMBINE);
        and = BasicComponentFactory.createComboBox(
                new SelectionInList<Object>(QueryInfo.combine_options, booleanChoiceModel));        

        
		FormLayout layout = new FormLayout(
            "50px, 10px, 100px, 5px, 50px, 5px, 150px, 10px, 80px, 120px",
           // "2*(pref, 2px, pref, 9px), pref, 2px, pref");
		 //"4*( pref, 3px, pref, 3px, pref, 5px), pref, 2px, pref");
		" 30*(pref), pref, 2px, pref");

		final int pos_left = 1;
		final int pos_labels = 3;
		final int pos_conditions = 5;
		final int pos_values = 7;
		final int pos_struc = 9;
		
        panel2d = new Panel2D();
        panel2d.setEditable(false);
        panel2d.setToolTipText("Click here to edit structure");
        panel2d.setBorder(BorderFactory.createEtchedBorder());
        
        PanelBuilder panel = new PanelBuilder(layout);
        panel.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();        
        panel.addSeparator("Identifier", cc.xyw(pos_left,  9 ,pos_values));
        
        panel.add(fieldnames1,    cc.xyw  (pos_labels,11,1));
        panel.add(fieldnames2,    cc.xy  (pos_labels,13));
        panel.add(fieldnames3,    cc.xy  (pos_labels,15));
        
        panel.add(cond1,    cc.xyw  (pos_conditions,11,1));
        panel.add(cond2,    cc.xyw  (pos_conditions,13,1));
        panel.add(cond3,    cc.xyw  (pos_conditions,15,1));
        
        panel.add(tf_identifier1,     cc.xy  (pos_values,  11));
        panel.add(tf_identifier2,       cc.xy  (pos_values,  13));
        panel.add(tf_identifier3,    cc.xy  (pos_values,  15));
        
        panel.addSeparator("Structure", cc.xyw(pos_left,  1, pos_values ));
        panel.add(new JLabel("Formula"),    cc.xy  (pos_labels,3));
        panel.add(tf_formula,       cc.xyw  (pos_conditions,  3,3));
        panel.add(new JLabel("SMILES"),    cc.xy  (pos_labels,5));
        panel.add(tf_smiles,      cc.xyw  (pos_conditions,  5,3));
        panel.add(new JLabel("INChI"),    cc.xy  (pos_labels,7));
        panel.add(tf_inchi,       cc.xyw  (pos_conditions, 7,3));

        panel.addSeparator("Combine by", cc.xyw(pos_left,  17, pos_conditions ));
        panel.add(and,       cc.xyw  (pos_labels, 19, 1));
        
        panel.addSeparator("Search by", cc.xyw(pos_values,  17,4 ));
        panel.add(method,       cc.xyw  (pos_values, 19, 1));
        JLabel tlabel = new JLabel("Threshold");
        tlabel.setToolTipText("Similarity threshold");
        panel.add(tlabel,    cc.xyw  (pos_struc,19,1));
        panel.add(threshold,       cc.xyw  (pos_struc+1, 19, 1));
        
        panel.addSeparator("Search within", cc.xyw(pos_left,  21,pos_struc+1 ));
        panel.add(scope,       cc.xyw  (pos_labels, 23, 3));
        panel.add(dataset,     cc.xyw  (pos_values, 23, 1));        
        
        panel.addSeparator("Structure diagram", cc.xyw(pos_struc,  1,2 ));
        //panel.add(new JLabel("Structure"),    cc.xy  (3, 1));
        panel.add(panel2d, cc.xywh(pos_struc, 3, 2, 13));
        panel2d.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
    			super.mouseClicked(e);        		
        		if (e.getClickCount()==1) {
        			/* UNCOMMENT TO USE JCHEMPAIN
        			editAction.setMolecule(getObject().getMolecule());
        			editAction.actionPerformed(null);
        			panel2d.setAtomContainer(editAction.getMolecule(), true);
        			object.setMolecule(editAction.getMolecule());
        			*/
        		}	
        	}
        });

        return panel.getPanel();
	}
	public QueryInfo getObject() {
		return object;
	}
	public void setObject(QueryInfo object) {
		this.object = object;
		if (detailsModel != null)
			detailsModel.setBean(object);		
		panel2d.setAtomContainer(object.getMolecule(), true);
	}
	public JComponent getJComponent() {
		return this;
	}

	public void setEditable(boolean editable) {
		// TODO Auto-generated method stub

	}

	public boolean isEditable() {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean confirm() {
		return true;
	}

}

class QueryInfoModel extends PresentationModel<QueryInfo> {
		/**
	 * 
	 */
	private static final long serialVersionUID = -8164685603352191330L;
		protected static SelectionInList<SourceDataset> datasets = new SelectionInList<SourceDataset> ();
		protected static SelectionInList<String> fieldnames = new SelectionInList<String>();
		
		public QueryInfoModel(ValueHolder model) {
			super(model);
        
		}
		/*
        private QueryInfoModel(QueryInfo query) {
            super(query);
            selectionInList = new SelectionInList<SourceDataset>(
                    query.getDatasets(),
                    getModel(QueryInfo.PROPERTY_DATASET));
        }
        */			
        public SelectionInList<SourceDataset> getDatasets() {
        	if (getBean() == null) 
        		return new SelectionInList<SourceDataset>();
        	else
        		return new SelectionInList<SourceDataset>(getBean().getDatasets());
        }			
        public SelectionInList<String> getFieldnames() {
        	if (getBean() == null) 
        		return new SelectionInList<String>();
        	else
        		return new SelectionInList<String>(getBean().getFieldnames());
        }	 
        @Override
        public void setBean(QueryInfo newBean) {
        	super.setBean(newBean);
        	
        	//datasets.setList(newBean.getDatasets());
        	//fieldnames.setList(newBean.getFieldnames());
        	//datasets.fireIntervalAdded(0,datasets.getSize());
        	//fieldnames.fireIntervalAdded(0,fieldnames.getSize());
        }
        
    	protected boolean isScope(String value) {
    		return QueryInfo.SCOPE_DATASET.equals(getModel(QueryInfo.PROPERTY_SCOPE).getValue());
    	}
}