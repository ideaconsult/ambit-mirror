package ambit2.ui.editors;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListModel;

import ambit2.base.interfaces.IAmbitEditor;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public abstract class ListEditor implements IAmbitEditor<ListModel> {

    /**
     * 
     */
    protected EditorPanel editor;

    public EditorPanel getEditor() {
	return editor;
    }

    protected JList list;

    public JList getList() {
	return list;
    }

    protected JToolBar bar;
    protected SelectionInList selectionInList;
    protected JComponent component;

    protected boolean editable;

    public boolean isEditable() {
	return editable;
    }

    public ListEditor() {
	this(null);
    }

    public ListEditor(ListModel listModel) {
	super();
	createComponents();
	component = buildPanel();
	setObject(listModel);
    }

    public JComponent getJComponent() {
	return component;
    }

    public void setObject(ListModel object) {
	if (object != null) {
	    list.setModel(object);
	    selectionInList.setListModel(object);
	}

    }

    public void addMouseListener(MouseListener listener) {
	list.addMouseListener(listener);
    }

    protected void handleDoubleClick(Object selection) {

    }

    protected void createComponents() {
	list = new JList();

	list.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseReleased(MouseEvent e) {
		if (e.getClickCount() == 2)
		    handleDoubleClick(selectionInList.getSelection());
	    }
	});
	list.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	selectionInList = new SelectionInList();
	selectionInList.addPropertyChangeListener("value", new PropertyChangeListener() {
	    public void propertyChange(PropertyChangeEvent evt) {
		try {
		    IAmbitEditor a = getEditor(evt.getNewValue());
		    if (a != null)
			a.getJComponent().setBorder(BorderFactory.createEtchedBorder());
		    editor.setEditor(a);

		} catch (Exception x) {
		    editor.setEditor(null);
		    x.printStackTrace();
		}

	    }
	});
	Bindings.bind(list, selectionInList);

	editor = new EditorPanel();
	// editor.setBorder(BorderFactory.createEtchedBorder());
	editor.setPreferredSize(new Dimension(400, 200));
	editor.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	bar = new JToolBar();
	bar.setFloatable(false);
	configureToolBar(bar);
    }

    public JComponent buildPanel() {
	FormLayout layout = new FormLayout("left:170dlu:grow,left:170dlu:grow", "top:pref:grow,24dlu,top:pref:grow");

	PanelBuilder builder = new PanelBuilder(layout);
	CellConstraints cc = new CellConstraints();
	builder.add(new JScrollPane(list), cc.xywh(1, 1, 2, 1));
	builder.add(editor, cc.xywh(1, 3, 2, 1));
	builder.add(bar, cc.xywh(1, 2, 2, 1));
	return builder.getPanel();
    }

    public Object getSelected() {
	return selectionInList.getValue();
    }

    protected void configureToolBar(JToolBar bar) {

	bar.add(new JButton(new Navigate("|<", selectionInList) {
	    public void actionPerformed(ActionEvent e) {
		list.setSelectionIndex(0);
	    }
	}));
	bar.add(new JButton(new Navigate("<", selectionInList) {
	    public void actionPerformed(ActionEvent e) {
		if (list.getSelectionIndex() < 1)
		    list.setSelectionIndex(0);
		else
		    list.setSelectionIndex(list.getSelectionIndex() - 1);
	    }
	}));
	bar.add(new JButton(new Navigate(">", selectionInList) {
	    public void actionPerformed(ActionEvent e) {
		if (list.getSelectionIndex() >= list.getSize())
		    list.setSelectionIndex(list.getSize() - 1);
		else
		    list.setSelectionIndex(list.getSelectionIndex() + 1);
	    }
	}));
	bar.add(new JButton(new Navigate(">|", selectionInList) {
	    public void actionPerformed(ActionEvent e) {
		list.setSelectionIndex(list.getSize() - 1);

	    }
	}));

	bar.addSeparator();

    }

    protected abstract IAmbitEditor getEditor(Object object);

    /*
     * protected ListCellRenderer getListCellRenderer(String[] columns) { return
     * new CustomListCellRenderer(columns); }
     */

    public boolean confirm() {
	return true;
    }

    public ListModel getObject() {
	return list.getModel();
    }

    public void setEditable(boolean editable) {
	// TODO Auto-generated method stub

    }

}

class CustomListCellRenderer<V> extends DefaultListCellRenderer {
    protected String[] columns;

    public CustomListCellRenderer(String[] columns) {
	this.columns = columns;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	    boolean cellHasFocus) {
	Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

	StringBuffer b = new StringBuffer();
	Class c = value.getClass();
	for (String column : columns) {
	    try {
		String name = "get" + BeanEditor.capitalizeFirstLetter(column);
		Method m = c.getMethod(name, null);
		if (m != null) {
		    Object o = m.invoke(value, null);
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

abstract class Navigate extends AbstractAction {
    protected SelectionInList list;
    /**
	 * 
	 */
    private static final long serialVersionUID = 3522101652589080995L;

    public Navigate(String caption, SelectionInList list) {
	super();
	putValue(NAME, caption);
	this.list = list;
    }

}