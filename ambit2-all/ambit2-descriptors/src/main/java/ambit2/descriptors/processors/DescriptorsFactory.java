/* DescriptorsFactory.java
 * Author: nina
 * Date: Feb 7, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2013  Ideaconsult Ltd.
 * 
 * Contact: jeliazkova.nina@gmail.com
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

package ambit2.descriptors.processors;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.PropertyAnnotations;
import ambit2.base.data.Template;
import ambit2.core.data.IObject2Properties;

public class DescriptorsFactory extends AbstractDescriptorFactory<Profile<Property>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 275242996048077139L;

	@Override
	protected synchronized void addToResult(String name, boolean enabled, int order, Profile<Property> result)
			throws Exception {
		Property property = createDescriptor2Property(name);
		if (property != null) {
			property.setEnabled(enabled);
			property.setOrder(order);
			result.add(property);
		}
	}

	@Override
	protected Profile<Property> createResult() {
		return new Template();
	}

	public static synchronized List<Property> createDescriptor2Properties(String className) throws Exception {
		Class clazz = DescriptorsFactory.class.getClassLoader().loadClass(className);
		Object o = clazz.newInstance();
		if (o instanceof IObject2Properties) {
			return ((IObject2Properties) o).process(o);
		} else if (o instanceof IMolecularDescriptor) {
			IMolecularDescriptor descriptor = (IMolecularDescriptor) o;
			List<Property> p = new ArrayList<Property>();
			// this is to remove swing listeners from toxtree rules
			try {
				o.getClass().getMethod("removeListener", new Class[] {}).invoke(o, new Object[] {});
			} catch (Exception x) {
				// x.printStackTrace();
			}
			DescriptorValue value = ((IMolecularDescriptor) o).calculate(MoleculeFactory.makeAlkane(2));
			for (String name : value.getNames()) {
				p.add(descriptorValue2Property(descriptor, name, value));
			}
			return p;
		} else
			return null;
	}

	public static synchronized Property descriptorValue2Property(IMolecularDescriptor descriptor, String name,
			DescriptorValue value) throws Exception {

		String label = value.getSpecification().getSpecificationReference();
		if (Property.opentox_InChI_std.equals(name) || Property.opentox_InChIKey_std.equals(name)
				|| Property.opentox_InChIAuxInfo_std.equals(name)) {
			label = name;
		}
		LiteratureEntry le = LiteratureEntry.getInstance(value.getSpecification().getImplementationTitle(), value
				.getSpecification().getSpecificationReference());
		le.setType(_type.Algorithm);
		Property property = new Property(name, le);

		property.setLabel(label);
		if (descriptor != null)
			property.setClazz(descriptor.getClass());

		return property;
	}

	public static synchronized Property createDescriptor2Property(String className) throws Exception {
		return createDescriptor2Property(className, null);
	}

	public static synchronized Property createDescriptor2Property(String className, String[] parameters)
			throws Exception {
		IMolecularDescriptor descriptor = createDescriptor(className, parameters);
		if (descriptor != null) {
			Property property = Property.getInstance(
					descriptor.getClass().getName().substring(descriptor.getClass().getName().lastIndexOf('.') + 1),

					LiteratureEntry.getInstance(descriptor.getSpecification().getImplementationTitle(), descriptor
							.getSpecification().getSpecificationReference()));
			property.setLabel(descriptor.getClass().getName());
			property.setClazz(descriptor.getClass());
			property.setAnnotations(new PropertyAnnotations());
			if (parameters != null)
				for (int i=0; i < parameters.length; i++) {
					PropertyAnnotation a = new PropertyAnnotation<>();
					a.setType("PARAMETER");
					a.setPredicate(Integer.toString(i));
					a.setObject(parameters[i]);
					property.getAnnotations().add(a);
				}

			return property;
		} else
			return null;
	}

	public static synchronized IMolecularDescriptor createDescriptor(String className) throws Exception {
		return createDescriptor(className, null);
	}

	public static synchronized IMolecularDescriptor createDescriptor(String className, String[] parameters)
			throws Exception {
		Class clazz = DescriptorsFactory.class.getClassLoader().loadClass(className);
		Object o = clazz.newInstance();
		if (o instanceof IMolecularDescriptor) {
			IMolecularDescriptor descriptor = (IMolecularDescriptor) o;
			try {
				if (parameters != null)
					descriptor.setParameters(parameters);
			} catch (Exception x) {
				// x.printStackTrace();
			}
			try {
				o.getClass().getMethod("removeListener", new Class[] {}).invoke(o, new Object[] {});
			} catch (Exception x) {
			}
			try {
				o.getClass().getMethod("setWeb", new Class[] { Boolean.class })
						.invoke(o, new Object[] { Boolean.TRUE });
			} catch (Exception x) {
			}
			return descriptor;
		} else
			return null;
	}
}
