/* DescriptorsFactory.java
 * Author: nina
 * Date: Feb 7, 2009
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

package ambit2.descriptors.processors;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.data.ILiteratureEntry._type;

public class DescriptorsFactory extends AbstractDescriptorFactory<Profile<Property>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 275242996048077139L;
	
	@Override
	protected void addToResult(String name, boolean enabled, int order,
			Profile<Property> result) throws Exception {
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

	public static List<Property> createDescriptor2Properties(String className) throws Exception  {
		Class clazz = DescriptorsFactory.class.getClassLoader().loadClass(className);
		//if (o instanceof IMolecularDescriptor) {verify for interface
			Object o = clazz.newInstance();
			if (o instanceof IMolecularDescriptor) {
				IMolecularDescriptor descriptor = (IMolecularDescriptor) o;
				List<Property> p = new ArrayList<Property>();
				//this is to remove swing listeners from toxtree rules
				try {
					o.getClass().getMethod(
			                "removeListener",
			                new Class[] {}).
			        invoke(o, new Object[] { });					
				} catch (Exception x) {
					//x.printStackTrace();
				}				
				DescriptorValue value = ((IMolecularDescriptor) o).calculate(MoleculeFactory.makeAlkane(2));
				for (String name : value.getNames()) {
					p.add(descriptorValue2Property(descriptor,name,value));
				}
				return p;				
			} else return null;
	}
	
	public static Property descriptorValue2Property(IMolecularDescriptor descriptor, String name, DescriptorValue value) throws Exception  {

		LiteratureEntry le = LiteratureEntry.getInstance(value.getSpecification().getImplementationTitle(),value.getSpecification().getSpecificationReference());
		le.setType(_type.Algorithm);
		Property property = new Property(name,le);


		property.setLabel(value.getSpecification().getSpecificationReference());
		if (descriptor!=null)
			property.setClazz(descriptor.getClass());

		return property;
	}
	public static Property createDescriptor2Property(String className) throws Exception  {
		IMolecularDescriptor descriptor = createDescriptor(className);
		if (descriptor!=null) {
			Property property = Property.getInstance(descriptor.getClass().getName().substring(descriptor.getClass().getName().lastIndexOf('.')+1),
					
					LiteratureEntry.getInstance(descriptor.getSpecification().getImplementationTitle(),descriptor.getSpecification().getSpecificationReference())
						);
			property.setLabel(descriptor.getClass().getName());
			property.setClazz(descriptor.getClass());
			return property;	
		} else return null;
	}
	
	public static IMolecularDescriptor createDescriptor(String className) throws Exception  {
			Class clazz = DescriptorsFactory.class.getClassLoader().loadClass(className);
			Object o = clazz.newInstance();
			if (o instanceof IMolecularDescriptor) {
				IMolecularDescriptor descriptor = (IMolecularDescriptor) o;
				
				try {
					o.getClass().getMethod(
			                "removeListener",
			                new Class[] {}).
			        invoke(o, new Object[] { });					
				} catch (Exception x) {
					//x.printStackTrace();
				}
				return descriptor;			
			} else return null;
	}	
}
