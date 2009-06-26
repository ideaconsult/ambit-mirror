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

import java.beans.IntrospectionException;
import java.io.InputStream;
import java.util.List;

import org.openscience.cdk.qsar.IMolecularDescriptor;

import ambit2.base.data.ClassHolder;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.DefaultAmbitProcessor;

public class DescriptorsFactory extends DefaultAmbitProcessor<String,Profile<Property>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 275242996048077139L;

	public Profile<Property> process(String target)
			throws AmbitException {
		Profile<Property> p = new Template();
		if (target==null)
			target="ambit2/descriptors/descriptors.txt";
		InputStream in = DescriptorsFactory.class.getClassLoader().getResourceAsStream(target);
		List<ClassHolder> classes = ClassHolder.load(in);
		for (int i=0; i < classes.size();i++) {
			try {
				boolean enabled = true;
				String name= classes.get(i).getClazz();
				if (classes.get(i).getClazz().indexOf(";")==0) {
					name = classes.get(i).getClazz().substring(1);
					enabled = false;
				}
				Class clazz = DescriptorsFactory.class.getClassLoader().loadClass(name);
				//if (o instanceof IMolecularDescriptor) {verify for interface
				try {
					Object o = clazz.newInstance();
					if (o instanceof IMolecularDescriptor) {
						IMolecularDescriptor descriptor = (IMolecularDescriptor) o;
						
						Property property = Property.getInstance(clazz.getName().substring(clazz.getName().lastIndexOf('.')+1),
								LiteratureEntry.getInstance(descriptor.getSpecification().getImplementationTitle(),descriptor.getSpecification().getSpecificationReference())
									);
						property.setLabel(clazz.getName());
						property.setOrder(i);
						property.setClazz(clazz);
						property.setEnabled(enabled);
						p.add(property);						
					}
				} catch (Exception x) {
					x.printStackTrace();
				}

				//}
;
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		return p;
	}

}
