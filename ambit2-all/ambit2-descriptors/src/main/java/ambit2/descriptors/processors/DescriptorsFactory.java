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

import java.io.InputStream;
import java.util.List;

import ambit2.core.data.ClassHolder;
import ambit2.core.data.Profile;
import ambit2.core.data.Property;
import ambit2.core.exceptions.AmbitException;
import ambit2.core.processors.DefaultAmbitProcessor;

public class DescriptorsFactory extends DefaultAmbitProcessor<String,Profile> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 275242996048077139L;

	public Profile process(String target)
			throws AmbitException {
		Profile p = new Profile();
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(target);
		List<ClassHolder> classes = ClassHolder.load(in);
		for (int i=0; i < classes.size();i++) {
			try {
				Class clazz = this.getClass().getClassLoader().loadClass(classes.get(i).getClazz());
				//if (o instanceof IMolecularDescriptor) {verify for interface
					Property property = new Property();
					property.setName(clazz.getName());
					property.setLabel("Descriptors");
					property.setOrder(i);
					property.setClazz(clazz);
					p.add(property);
				//}
			} catch (Exception x) {
				
			}
		}
		return p;
	}

}
