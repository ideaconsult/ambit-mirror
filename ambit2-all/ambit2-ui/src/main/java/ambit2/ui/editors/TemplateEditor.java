package ambit2.ui.editors;

import javax.swing.JComponent;

import ambit2.base.data.Profile;
import ambit2.base.data.ProfileListModel;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IAmbitEditor;

public class TemplateEditor implements IAmbitEditor<Profile<Property>> {
	protected Profile<Property> profile;
	protected SelectFieldsPanel panel = new SelectFieldsPanel();
	public boolean confirm() {
		return panel.confirm();
	}

	public JComponent getJComponent() {
		return panel;
	}

	public Profile<Property> getObject() {
		return profile;
	}

	public boolean isEditable() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setEditable(boolean editable) {
		// TODO Auto-generated method stub
		
	}

	public void setObject(Profile<Property> object) {
		panel.setObject(new ProfileListModel(object,null));
		this.profile = object;
	}

}
