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

package ambit2.config;

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import ambit2.ui.editors.IAmbitEditor;

public class EditorPreferences {
	protected static PropertyChangeSupport propertyChangeSupport;
	
	protected final static String filename="ambit2.ui.pref";
	protected static Properties props = null;
	public static Object[][] default_values = {
		
		{"com.microworkflow.process.ValueLatchPair","ambit2.workflow.ui.ValueLatchPairEditor"},
		{"ambit2.repository.LoginInfo","ambit2.data.qmrf.QMRFAttributesPanel"},
		{"ambit2.data.literature.LiteratureEntry","ambit2.data.qmrf.QMRFAttributesPanel"},
		{"ambit2.data.molecule.SourceDataset","ambit2.ui.editors.SourceDatasetEditor"},		
	};
	
	protected static Properties getDefault() {
		Properties p = new Properties();
		for (int i=0; i < default_values.length; i++) {
			p.setProperty(default_values[i][0].toString(),default_values[i][1].toString());			
		}
		return p;
	}
	protected static Properties loadProperties() throws IOException {
		Properties p = new Properties();
		InputStream in = new FileInputStream(new File(filename));
		p.load(in);
		in.close();
		return p;
	}
	public static Properties getProperties() {
		if (props == null)  
			try {
				props = loadProperties();
				if (props.size()==0)
					props = getDefault();
				propertyChangeSupport = new PropertyChangeSupport(props);
			} catch (Exception x) {
				props = getDefault();
				propertyChangeSupport = new PropertyChangeSupport(props);
			}
		return props;
	}

	public static void saveProperties(String comments) throws IOException {
		if (props == null) return;
		OutputStream out = new FileOutputStream(new File(filename));
		props.store(out,comments);
		out.close();
	}
	public static void setProperty(String key, String value) {
		Properties p = getProperties();
		String oldValue = p.getProperty(key);
		p.put(key, value);
		propertyChangeSupport.firePropertyChange(key, oldValue, value);
	}
	public static String getProperty(String key) {
		Properties p = getProperties();
		return p.get(key).toString();
	}
	public static PropertyChangeSupport getPropertyChangeSupport() {
		if (propertyChangeSupport == null)
			getProperties();
		return propertyChangeSupport;
	}
	public static void setPropertyChangeSupport(
			PropertyChangeSupport propertyChangeSupport) {
		Preferences.propertyChangeSupport = propertyChangeSupport;
	}
	public static IAmbitEditor getEditor(Class key) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		return getEditor(key.getName());
	}	
	public static IAmbitEditor getEditor(Object key) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		IAmbitEditor e = getEditor(key.getClass().getName());
		if (e != null)
			e.setObject(key);
		return e;
	}
	
	public static IAmbitEditor getEditor(String key) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		String s = getProperty(key);
		Class clazz = Class.forName(s);
		Object o = clazz.newInstance();
		if (o instanceof IAmbitEditor) {
			return (IAmbitEditor)o;
		}
		else return null;
	}
}


