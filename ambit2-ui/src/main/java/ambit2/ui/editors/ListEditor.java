package ambit2.ui.editors;

import java.awt.Component;
import java.lang.reflect.Method;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.IndirectListModel;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class ListEditor<L extends List<V>,V> extends JPanel implements IAmbitEditor<L>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3115720101465584356L;


    /**
     * Holds the edited Album and vends ValueModels that adapt Album properties.
     */
    protected JComponent[] fields;
    protected String[] columns = {"Name"};
    protected String detailsCaption = "Details";	
    protected PresentationModel<V> detailsModel;	
    private IndirectListModel<V> listModel;
    private JList       objectList;
    protected String listCaption = "List";
    protected boolean editable;
    
	public boolean isEditable() {
		return editable;
	}

	public ListEditor() {
		this(null,null,"List","Details");
	}
	public ListEditor(L list,final String[] columns,  String listCaption, String detailsCaption) {
		this(list,columns,columns,listCaption,detailsCaption);
	}
	public ListEditor(L list,final String[] columns, final String[] captions, String listCaption, String detailsCaption) {
		super();
		//setLayout(new BorderLayout());
		setListCaption(listCaption);
		setDetailsCaption(detailsCaption);
		add(buildPanel(list, columns,captions));
	}
	protected PresentationModel createPresentationModel() {
		return new PresentationModel<V>(new ValueHolder(null, true));
	}	
	protected void initComponents(L list,final String[] columns , final String[] captions) {
		this.columns = columns;
		setObject(list);		
        detailsModel = new PresentationModel<V>(new ValueHolder(null, true));		
		fields = new JTextComponent[columns.length];
		for (int i=0; i < columns.length;i++) {
		    JTextField t = BasicComponentFactory.createTextField(
	                detailsModel.getModel(columns[i]));
	        t.setEditable(false);
	        fields[i] = t;
		}
	}
	  public JComponent buildPanel(L list,final String[] columns, final String[] captions) {
	        initComponents(list,columns,captions);

	        FormLayout layout = new FormLayout(
	                "right:pref, 3dlu, 150dlu:grow",
	                "p, 1dlu, p, 9dlu, p, 1dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 9dlu, p");

	        PanelBuilder builder = new PanelBuilder(layout);
	        builder.setDefaultDialogBorder();
	        CellConstraints cc = new CellConstraints();

	        builder.addSeparator(listCaption,  cc.xyw(1,  1, 3));
	        builder.add(new JScrollPane(
	        		objectList),    cc.xy (3,  3));


	        builder.addSeparator(detailsCaption, cc.xyw(1,  5, 3));
	        int row = 7;
	        for (int i=0; i < columns.length;i ++) {
		        builder.addLabel(BeanEditor.capitalizeFirstLetter(captions[i]), cc.xy (1,  row));
		        builder.add(fields[i],cc.xy (3,  row));
		        row += 2;
	        }
		
	        return builder.getPanel();
	    }
	  
	
    private void initEventHandling() {
    	objectList.addListSelectionListener(new ListSelectionHandler());
    }	
    
    private class ListSelectionHandler implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting())
                return;
            detailsModel.setBean((V) objectList.getSelectedValue());
        }
    }    
	public void setObject(L object) {
		if (listModel == null)
			listModel = new IndirectListModel<V>(object);
		else {
			listModel.setList(object);
		}
		if (objectList == null) {
			objectList = new JList(listModel);
			setLayoutOrientation(JList.HORIZONTAL_WRAP);
			objectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			objectList.setCellRenderer(getListCellRenderer(columns));
			initEventHandling();
		}
		

	}

	protected ListCellRenderer getListCellRenderer(String[] columns) {
		return new CustomListCellRenderer(columns);
	}
	public L getObject() {
		return (L)listModel.getList();
	}

	public JComponent getJComponent() {
		return this;
	}

	
    public V getSelectedValue() {
    	return (V)objectList.getSelectedValue();
    }
	public String getListCaption() {
		return listCaption;
	}
	public void setListCaption(String listCaption) {
		this.listCaption = listCaption;
	}
	public String getDetailsCaption() {
		return detailsCaption;
	}
	public void setDetailsCaption(String detailsCaption) {
		this.detailsCaption = detailsCaption;
	}
	public String[] getColumns() {
		return columns;
	}
	public void setColumns(String[] columns) {
		this.columns = columns;
	}	
	public void setEditable(boolean editable) {
		this.editable = editable;
		for (JComponent field : fields)
			if (field instanceof JTextField) ((JTextField) field).setEditable(editable);
			else if (field instanceof JFormattedTextField) ((JFormattedTextField) field).setEditable(editable);
	}
    public void setLayoutOrientation(int layoutOrientation) {
    	if (objectList != null)
    		objectList.setLayoutOrientation(layoutOrientation);
    }
    public void setCellRenderer(ListCellRenderer cellRenderer) {
    	if (objectList != null)
    		objectList.setCellRenderer(cellRenderer);
    }
}

class CustomListCellRenderer<V> extends DefaultListCellRenderer {
	protected String[] columns;
	public CustomListCellRenderer(String[] columns) {
		this.columns = columns;
	}
    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list,
                value, index, isSelected, cellHasFocus);

        StringBuffer b = new StringBuffer();
        Class c = value.getClass();
        for (String column: columns) {
        	try {
        		String name = "get"+BeanEditor.capitalizeFirstLetter(column);
	        	Method m = c.getMethod(name, null);
	        	if (m != null) {
	        		Object o = m.invoke(value,null);
	        		if (o != null)
	        			b.append(o.toString());
	        	}
        	} catch (Exception x) {
        		x.printStackTrace();
        	}
        	b.append(" ");
        }
        setText(b.toString());
        return component;
    }


}
