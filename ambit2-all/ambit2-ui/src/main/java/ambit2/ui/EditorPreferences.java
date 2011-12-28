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

package ambit2.ui;

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import ambit2.base.config.Preferences;
import ambit2.base.interfaces.IAmbitEditor;

public class EditorPreferences {
	protected static PropertyChangeSupport propertyChangeSupport;
	
	protected final static String filename="ambit2.ui.pref";
	protected static Properties props = null;
	public static Object[][] default_values = {
		
		{"com.microworkflow.process.ValueLatchPair","ambit2.workflow.ui.ValueLatchPairEditor"},
		{"ambit2.dbui.LoginInfoBean","ambit2.dbui.LoginPanel"},
		//{"ambit2.core.data.LiteratureEntry","ambit2.ui.editors.ReferenceEditor"},
		{"ambit2.base.data.SourceDataset","ambit2.dbui.SourceDatasetEditor"},
		{"ambit2.base.data.AmbitUser","ambit2.ui.editors.AmbitUserEditor"},
		//{"java.lang.StringBuffer","ambit2.ui.query.DbStatusPanel"},
		{"ambit2.base.data.Template","ambit2.ui.editors.TemplateEditor"},
		{"ambit2.base.data.Property","ambit2.ui.editors.PropertyEditor"},
		{"ambit2.base.data.Dictionary","ambit2.ui.editors.DictionaryEditor"},
		{"ambit2.db.search.property.QueryOntology","ambit2.dbui.dictionary.OntologyEditor"},
		{"ambit2.core.data.experiment.Study","ambit2.ui.editors.StudyEditor"},
		{"ambit2.core.data.experiment.Experiment","ambit2.ui.editors.ExperimentEditor"},
		//{"toxTree.core.IDecisionMethod","ambit2.ui.editors.ToxTreeEditor"},
		{"ambit2.db.search.structure.QueryField","ambit2.dbui.QueryFieldEditor"},
		{"ambit2.db.search.structure.QueryFieldNumeric","ambit2.dbui.QueryFieldNumericEditor"},
		{"ambit2.db.search.structure.QueryDataset","ambit2.dbui.QueryDatasetEditor"},
		{"ambit2.db.search.structure.QueryCombinedStructure","ambit2.dbui.QueryCombinedEditor"},
		{"ambit2.db.search.structure.QueryStructure","ambit2.dbui.QueryChemicalEditor"},
		{"ambit2.db.search.structure.QuerySimilarityStructure","ambit2.dbui.QuerySimilarityEditor"},
		{"ambit2.db.search.structure.QuerySMARTS","ambit2.dbui.QuerySmartsEditor"},		
		
		{"ambit2.db.search.DictionarySubjectQuery","ambit2.dbui.dictionary.DictionaryQueryPanel"},
		{"ambit2.db.search.DictionaryObjectQuery","ambit2.dbui.dictionary.DictionaryQueryPanel"},
		{"ambit2.db.search.DictionaryQuery","ambit2.dbui.dictionary.DictionaryQueryPanel"},
		{"ambit2.db.search.structure.QueryStoredResults","ambit2.dbui.QueryStoredResultsEditor"},
		{"ambit2.db.search.structure.QueryFunctionalGroups","ambit2.dbui.QueryFunctionalGroupsEditor"},
		
		{"ambit2.db.search.structure.QueryStructureByQuality","ambit2.dbui.QueryStructureByQualityEditor"},
		{"ambit2.db.search.structure.QueryStructureByValueQuality","ambit2.dbui.QueryStructureByValueQualityEditor"},
	//	{"ambit2.db.search.structure.QueryStructureByQualityPairLabel","ambit2.dbui.QueryStructureByConsensusEditor"},
		
		{"ambit2.db.search.structure.ScopeQuery","ambit2.dbui.QueryScopeEditor"},
		
		{"ambit2.base.data.StructureRecord","ambit2.workflow.ui.StructureRecordEditor"},		
		
		{"ambit2.db.search.StoredQuery","ambit2.workflow.ui.StoredQueryResultsPanel"},
		{"ambit2.db.search.QueryInfo","ambit2.dbui.QueryInfoEditor"},
		{"ambit2.base.data.StringBean","ambit2.ui.editors.StringBeanEditor"},
		{"ambit2.core.io.FileInputState","ambit2.ui.editors.FileSelector"},
		{"ambit2.core.io.FileOutputState","ambit2.ui.editors.FileSelector"},
		{"org.openscience.cdk.Molecule","ambit2.ui.editors.Panel2D"},		
		{"org.openscience.cdk.AtomContainer","ambit2.ui.editors.Panel2D"},		
		{"ambit2.base.data.ProfileListModel","ambit2.ui.editors.SelectFieldsPanel"},
		{"ambit2.base.data.Profile","ambit2.dbui.ProfileEditor"},
		{"ambit2.base.data.Endpoints","ambit2.dbui.ProfileEditor"},		
		
		{"ambit2.base.data.SelectionBean","ambit2.workflow.ui.SingleSelectionEditor"}		
		

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
		Object v = p.get(key);
		if (v == null) return null;
		else 
			return p.get(key).toString();
	}
	public static PropertyChangeSupport getPropertyChangeSupport() {
		if (propertyChangeSupport == null)
			getProperties();
		return propertyChangeSupport;
	}
	public static void setPropertyChangeSupport(
			PropertyChangeSupport propertyChangeSupport) {
		Preferences.setPropertyChangeSupport(propertyChangeSupport);
	}
	public static IAmbitEditor getEditor(Class key) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		if (key == null) return null;
		return getEditor(key.getName());
	}	
	public static IAmbitEditor getEditor(Object key) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		if (key == null) return null;
		IAmbitEditor e = getEditor(key.getClass().getName());
		if (e != null)
			e.setObject(key);
		return e;
	}
	
	public static IAmbitEditor getEditor(String key) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		
		String s = getProperty(key);
		if (s == null) return null;
		Class clazz = Class.forName(s);
		Object o = clazz.newInstance();
		if (o instanceof IAmbitEditor) {
			return (IAmbitEditor)o;
		}
		else return null;
	}
}


