/* ProfileEditor.java
 * Author: nina
 * Date: Apr 17, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.dbui;

import java.sql.Connection;

import javax.swing.JComponent;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.AbstractDBProcessor;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IAmbitEditor;
import ambit2.dbui.dictionary.DictionaryQueryPanel;

public class ProfileEditor extends AbstractDBProcessor<Profile<Property>, Profile<Property>> implements
	IAmbitEditor<Profile<Property>> {
    /**
	 * 
	 */
    private static final long serialVersionUID = 6665474866800960721L;
    protected Profile<Property> profile;
    protected DictionaryQueryPanel panel;

    public ProfileEditor() {
	panel = new DictionaryQueryPanel();
    }

    public boolean confirm() {
	return panel.confirm();
    }

    public JComponent getJComponent() {
	return panel.getJComponent();
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
	this.profile = object;
	panel.setProfile(profile);

    }

    public Profile<Property> process(Profile<Property> target) throws AmbitException {
	return target;
    }

    public void open() throws DbAmbitException {
	panel.open();

    }

    @Override
    public void setConnection(Connection connection) throws DbAmbitException {
	super.setConnection(connection);
	panel.setConnection(connection);
    }

    @Override
    public void close() throws Exception {
	super.close();
	panel.close();
    }

}
