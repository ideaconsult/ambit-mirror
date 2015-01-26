/* AmbitRowsEventTest.java
 * Author: nina
 * Date: Apr 5, 2009
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

package ambit2.db.results.test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import junit.framework.Assert;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;

import org.dbunit.database.IDatabaseConnection;
import org.junit.Test;

import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.db.processors.test.DbUnitTest;
import ambit2.db.readers.RetrieveField;
import ambit2.db.results.AmbitRows;
import ambit2.db.results.QueryChangeEvent;
import ambit2.db.results.RowsModel;
import ambit2.db.search.DictionaryObjectQuery;
import ambit2.db.search.property.TemplateQuery;
import ambit2.db.search.structure.QueryField;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.adapter.ComboBoxAdapter;
import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;

public class AmbitRowsEventTest extends DbUnitTest {
    int top = 0;
    int dict = 0;
    int prop = 0;
    int obj = 0;

    protected String getTestDatabase() {
	return "src/test/resources/ambit2/db/processors/test/dataset-properties.xml";
    }

    protected AmbitRows<Dictionary> createDictionaryQuery() throws Exception {
	AmbitRows<Dictionary> dictionary = new AmbitRows<Dictionary>() {
	    @Override
	    protected IQueryRetrieval createNewQuery(Dictionary target) throws AmbitException {
		// retrieve field names given a template
		TemplateQuery q = new TemplateQuery();
		q.setValue(target.getTemplate());
		Assert.assertEquals("BCF", target.getTemplate());
		return q;
	    }

	    @Override
	    public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt instanceof QueryChangeEvent)
		    top++;
		try {
		    while (next())
			process(getObject());
		} catch (Exception x) {
		    Assert.fail(x.getMessage());
		}

	    }

	};
	dictionary.setPropertyname("dictionary");
	return dictionary;
    }

    @Test
    public void atest() {

    }

    protected AmbitRows<Property> createPropertyQuery() throws Exception {
	AmbitRows<Property> properties = new AmbitRows<Property>() {
	    /**
		     * 
		     */
	    private static final long serialVersionUID = -2751315311469855394L;

	    @Override
	    protected IQueryRetrieval createNewQuery(Property target) throws AmbitException {
		// retrieve values given property name
		Assert.assertTrue("Property 1".equals(target.getName()) || "Property 2".equals(target.getName())
			|| "Property 3".equals(target.getName()));
		RetrieveField q = new RetrieveField();
		q.setFieldname(target);
		q.setValue(new StructureRecord(-1, 100215, null, null));
		/*
		 * RetrieveFieldNames q = new RetrieveFieldNames();
		 * q.setFieldname("name"); q.setValue(target);
		 */
		return q;

	    }

	    @Override
	    public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt instanceof QueryChangeEvent)
		    dict++;
		try {
		    while (next())
			process(getObject());
		} catch (Exception x) {
		    Assert.fail(x.getMessage());
		}

	    }

	};
	properties.setPropertyname("properties");
	return properties;
    }

    protected AmbitRows<Object> createValueQuery() throws Exception {
	final AmbitRows<Object> property = new AmbitRows<Object>() {
	    @Override
	    protected synchronized IQueryRetrieval createNewQuery(Object target) throws AmbitException {
		return null;

	    }

	    @Override
	    public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);

		try {
		    if (evt instanceof QueryChangeEvent)
			prop++;
		    while (next()) {
			Assert.assertEquals(12.0, Double.parseDouble(getObject().toString()), 1E-4);
			obj++;
		    }
		} catch (Exception x) {
		    Assert.fail(x.getMessage());
		}

	    }
	};
	property.setPropertyname("property");
	return property;
    }

    public void test() throws Exception {
	top = 0;
	dict = 0;
	prop = 0;
	obj = 0;
	setUpDatabase(getTestDatabase());
	PropertyChangeSupport ps = new PropertyChangeSupport(this);

	final AmbitRows<Dictionary> dictionary = createDictionaryQuery();
	ps.addPropertyChangeListener("topquery", dictionary);

	final AmbitRows<Property> properties = createPropertyQuery();
	dictionary.addPropertyChangeListener(dictionary.getPropertyname(), properties);

	final AmbitRows<Object> property = createValueQuery();
	properties.addPropertyChangeListener(properties.getPropertyname(), property);

	IDatabaseConnection c = getConnection();
	try {
	    dictionary.setConnection(c.getConnection());
	    properties.setConnection(c.getConnection());
	    property.setConnection(c.getConnection());
	    DictionaryObjectQuery q = new DictionaryObjectQuery();
	    q.setValue("All");
	    ps.firePropertyChange(new QueryChangeEvent(this, "topquery", null, q));

	} finally {
	    Assert.assertEquals(1, top);
	    Assert.assertEquals(1, dict);
	    Assert.assertEquals(3, prop);
	    Assert.assertEquals(2, obj);
	    c.close();
	}

    }

    public static void main(String[] args) {
	// http://www.java2s.com/Code/Java/Swing-Components/JGoodiesBindingSelectionInListModelExample.htm
	try {
	    final AmbitRows<Dictionary> dictionary = new AmbitRows<Dictionary>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6274513733204291475L;

		@Override
		protected synchronized IQueryRetrieval createNewQuery(Dictionary target) throws AmbitException {
		    TemplateQuery q = new TemplateQuery();
		    q.setValue(target.getTemplate());
		    return q;
		}
	    };

	    final AmbitRows<Property> properties = new AmbitRows<Property>() {
		@Override
		protected synchronized IQueryRetrieval createNewQuery(Property target) throws AmbitException {
		    QueryField q = new QueryField();
		    q.setFieldname(target);
		    return q;
		}

	    };
	    dictionary.addPropertyChangeListener(dictionary.getPropertyname(), properties);
	    dictionary.addPropertyChangeListener("status", properties);

	    AmbitRowsEventTest test = new AmbitRowsEventTest();
	    test.setUpDatabase(test.getTestDatabase());
	    IDatabaseConnection c = test.getConnection("localhost", "ambit2", "3306", "guest", "guest");
	    dictionary.setConnection(c.getConnection());
	    properties.setConnection(c.getConnection());

	    DictionaryObjectQuery q = new DictionaryObjectQuery();
	    q.setValue("Dataset");

	    PropertyChangeSupport ps = new PropertyChangeSupport(test);
	    ps.addPropertyChangeListener("topquery", dictionary);
	    ps.firePropertyChange(new QueryChangeEvent(test, "topquery", null, q));

	    final QueryField qf = new QueryField();
	    properties.addPropertyChangeListener(properties.getPropertyname(), new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {

		    try {
			IQueryRetrieval r = properties.process((Property) evt.getNewValue());
			qf.setFieldname(((QueryField) r).getFieldname());
		    } catch (AmbitException x) {
			x.printStackTrace();
		    }
		}
	    });

	    ListModel listModel = new RowsModel<Dictionary>(dictionary);
	    ValueModel selectedItemHolder1 = new ValueHolder();
	    SelectionInList<Dictionary> selectionInList = new SelectionInList<Dictionary>(listModel,
		    selectedItemHolder1);

	    final JList jlist = new JList();
	    Bindings.bind(jlist, selectionInList);

	    BeanAdapter beanAdapter = new BeanAdapter(selectionInList);
	    ValueModel nameModel = beanAdapter.getValueModel("template");
	    ValueModel parentModel = beanAdapter.getValueModel("parentTemplate");

	    ListModel listModel2 = new RowsModel<Property>(properties);
	    ValueModel selectedItemHolder2 = new ValueHolder();
	    SelectionInList<Property> selectionInList2 = new SelectionInList<Property>(listModel2, selectedItemHolder2);

	    BeanAdapter beanAdapter2 = new BeanAdapter(qf);
	    ValueModel fieldnameModel = beanAdapter2.getValueModel("fieldname");
	    // ValueModel conditionModel =
	    // beanAdapter2.getValueModel("condition");
	    ValueModel valueModel = beanAdapter2.getValueModel("value");

	    selectionInList.addPropertyChangeListener("value", new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
		    try {
			dictionary.process((Dictionary) evt.getNewValue());
		    } catch (Exception x) {
			x.printStackTrace();
		    }

		}
	    });

	    selectionInList2.addPropertyChangeListener("value", new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
		    try {

		    } catch (Exception x) {
			x.printStackTrace();
		    }

		}
	    });
	    JPanel p = new JPanel();

	    p.add(BasicComponentFactory.createTextField(nameModel));
	    p.add(BasicComponentFactory.createTextField(parentModel));

	    p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
	    p.add(new JLabel("Dictionary"));
	    p.add(new JScrollPane(jlist));
	    // p.add(new JComboBox(new
	    // ComboBoxAdapter<Dictionary>(selectionInList)));
	    p.add(new JLabel("Properties"));
	    final JComboBox combo = new JComboBox(new ComboBoxAdapter<Property>(selectionInList2));
	    p.add(combo);
	    p.add(BasicComponentFactory.createTextField(fieldnameModel));
	    // p.add(BasicComponentFactory.createTextField(conditionModel));
	    p.add(BasicComponentFactory.createIntegerField(valueModel));

	    properties.addPropertyChangeListener("status", new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
		    combo.setEnabled((Boolean) evt.getNewValue());
		}
	    });
	    JOptionPane.showMessageDialog(null, p);
	    c.close();

	} catch (Exception x) {
	    x.printStackTrace();
	}
    }
}
