/*
/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
 
 */
package ambit2.ui.editors;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import ambit2.base.interfaces.IAmbitEditor;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Generic editor
 * 
 * @author nina
 * 
 * @param <L>
 */
public class BeanEditor<L> implements IAmbitEditor<L> {
    protected JComponent component;
    protected JComponent[] fields;
    protected String[] columns = { "Name" };
    protected String[] captions = { "Name" };
    protected String detailsCaption = "Details";
    protected PresentationModel<L> detailsModel;
    protected L object;
    protected boolean editable = true;

    /**
	 * 
	 */

    public void setObject(L object) {
	this.object = object;
	if (detailsModel != null)
	    detailsModel.setBean(object);
    }

    public L getObject() {
	return object;
    }

    public JComponent getJComponent() {
	return component;
    }

    public BeanEditor(L object, final String[] columns, String detailsCaption) {
	this(object, columns, columns, detailsCaption);
    }

    public BeanEditor(L object, final String[] columns, String[] captions, String detailsCaption) {
	super();
	setDetailsCaption(detailsCaption);
	component = buildPanel(object, columns, captions);
    }

    protected PresentationModel createPresentationModel() {
	return new PresentationModel<L>(new ValueHolder(null, true));
    }

    protected void initComponents(L object, final String[] columns, String[] captions) {
	this.columns = columns;
	this.captions = captions;
	setObject(object);
	detailsModel = createPresentationModel();
	fields = new JTextComponent[columns.length];
	for (int i = 0; i < columns.length; i++) {
	    if (null == columns[i])
		fields[i] = null;
	    else {
		if (columns[i].toLowerCase().equals("password")) {
		    JPasswordField t = BasicComponentFactory.createPasswordField(detailsModel.getModel(columns[i]));
		    t.setEditable(true);
		    fields[i] = t;
		} else {
		    JTextField t = BasicComponentFactory.createTextField(detailsModel.getModel(columns[i]), true);
		    t.setEditable(isEditable());
		    fields[i] = t;
		}
	    }
	}
    }

    public void buildFields(PanelBuilder builder, CellConstraints cc) {

	builder.addLabel(detailsCaption, cc.xyw(1, 1, 3));
	int row = 3;
	for (int i = 0; i < columns.length; i++) {
	    if (null == columns[i]) {
		builder.addSeparator(captions[i], cc.xyw(1, row, 3));
		row++;
	    } else {
		builder.addLabel(capitalizeFirstLetter(captions[i]), cc.xy(1, row));
		builder.add(fields[i], cc.xy(3, row));
		row += 2;
	    }
	}
    }

    public JComponent buildPanel(L object, final String[] columns, String[] captions) {
	initComponents(object, columns, captions);

	StringBuffer b = new StringBuffer();
	b.append("p, 1dlu,");
	for (int i = 0; i < columns.length; i++) {
	    if (null == columns[i])
		b.append("12dlu,");
	    else {
		b.append("p, ");
		b.append("3dlu, ");
	    }
	}
	FormLayout layout = new FormLayout("right:pref, 3dlu, 150dlu:grow", b.toString());
	// "p, 1dlu, p, 9dlu, p, 1dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 9dlu, p");

	PanelBuilder builder = new PanelBuilder(layout);
	// builder.setDefaultDialogBorder();
	CellConstraints cc = new CellConstraints();

	buildFields(builder, cc);

	return builder.getPanel();
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

    public static String capitalizeFirstLetter(String column) {
	return column.substring(0, 1).toUpperCase() + column.substring(1);
    }

    public void setEditable(boolean editable) {
	this.editable = editable;
	for (JComponent field : fields)
	    if (field instanceof JTextField)
		((JTextField) field).setEditable(editable);
	    else if (field instanceof JFormattedTextField)
		((JFormattedTextField) field).setEditable(editable);
    }

    public boolean isEditable() {
	return editable;
    }

    public boolean confirm() {
	return true;
    }
}
