package ambit2.dbui;



import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import net.idea.modbcum.i.IDBProcessor;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.AbstractDBProcessor;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IAmbitEditor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.QueryCombined.COMBINE;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.SCOPE;
import ambit2.ui.EditorPreferences;
import ambit2.ui.editors.ListEditor;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.IndirectListModel;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class QueryCombinedEditor extends AbstractDBProcessor<IQueryRetrieval<Property>, QueryCombinedStructure> implements IAmbitEditor<QueryCombinedStructure> {
	protected ListEditor listEditor;
	protected PresentationModel<QueryCombinedStructure> model;
  
	/**
	 * 
	 */
	private static final long serialVersionUID = 2731638693126278344L;

	public QueryCombinedEditor() {
		super();
		model = new PresentationModel(null);	
		listEditor = new QueryListEditor() {
			@Override
			protected IAmbitEditor getEditor(Object object) {
				try {
					IAmbitEditor editor = EditorPreferences.getEditor(object);
					if (editor instanceof IDBProcessor)
						((IDBProcessor)editor).setConnection(getConnection());
					return editor;
				} catch (Exception x) {
					return null;
				}
			}
			@Override
			protected void configureToolBar(JToolBar bar) {
				super.configureToolBar(bar);
				SelectionInList<COMBINE> combine = new SelectionInList<COMBINE>(COMBINE.values(), 
												model.getModel("combine_queries"));
				bar.add(BasicComponentFactory.createComboBox(combine));
				bar.addSeparator();
				ValueHolder valueHolder = new ValueHolder();
				SelectionInList<SCOPE> scope = new SelectionInList<SCOPE>(SCOPE.values(),valueHolder);
				scope.addPropertyChangeListener("value",new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {

						try {
							IQueryRetrieval<IStructureRecord> scopeQuery = ((SCOPE)evt.getNewValue()).createQuery();
							setScope(scopeQuery);							
							//scopePanel.setEditor(EditorPreferences.getEditor(scopeQuery));
						} catch (Exception x) {
							
						}
					}
				});
				bar.add(new JLabel("Search within "));
				bar.add(BasicComponentFactory.createComboBox(scope));
				bar.addSeparator();				
				//bar.add(new JButton("Add"));
				//bar.add(new JButton("Update"));
				//bar.add(new JButton("Delete"));				
			}

		};
	}
	protected void setScope(IQueryRetrieval<IStructureRecord> scopeQuery) {
		try {
			getObject().setScope(scopeQuery);

		} catch (Exception x) {
			
		}
	}
	public JComponent getJComponent() {
		return listEditor.getJComponent();
	}

	public QueryCombinedStructure getObject() {
		return model.getBean();
	}

	public boolean isEditable() {
		return true;
	}

	public void setEditable(boolean editable) {
	}

	public void setObject(QueryCombinedStructure object) {
		listEditor.setObject(new IndirectListModel<IQueryRetrieval<IStructureRecord>>(object.getQueries()));
		model.setBean(object);
	}
	public boolean confirm() {
		return true;
	}
	public QueryCombinedStructure process(IQueryRetrieval<Property> target)
			throws AmbitException {
		return getObject();
	}
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}	

}

abstract class  QueryListEditor extends ListEditor {
	//protected EditorPanel scopePanel;
	@Override
	protected void createComponents() {

		super.createComponents();
		/*
		scopePanel = new EditorPanel();
		scopePanel.setBorder(BorderFactory.createTitledBorder("Search within"));
		scopePanel.setPreferredSize(new Dimension(100,100));
		*/
	}
	@Override
	public JComponent buildPanel() {
        FormLayout layout = new FormLayout(
	            "left:170dlu:grow,left:170dlu:grow",
				"top:pref:grow,top:pref:grow,24dlu,top:pref:grow"); 	        		
        
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();
        builder.add(new JScrollPane(list),cc.xywh(1,2,2,1));
        builder.add(editor,cc.xywh(1,1,2,1));
        builder.add(bar,cc.xywh(1,3,2,1));
       // builder.add(scopePanel,cc.xywh(1,4,2,1));
        return builder.getPanel();
	}
}

