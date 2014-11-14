/*
Copyright (C) 2005-2006  

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

package ambit2.descriptors.processors;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorEngine;
import org.openscience.cdk.qsar.DescriptorSpecification;
/**
 * Calculates descriptors found in cdk jars. Uses {@link DescriptorEngine}
 * @author Nina Jeliazkova
 *
 */
public class DescriptorEngineProcessor extends DefaultAmbitProcessor<IAtomContainer, List<DescriptorSpecification>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3482009837842377453L;
	protected DescriptorEngine engine;
	public DescriptorEngineProcessor() {
		super();
		engine = new DescriptorEngine(DescriptorEngine.MOLECULAR);

	}
	public DescriptorEngine getDescriptorEngine() {
		return engine;
	}
	public List<DescriptorSpecification> process(IAtomContainer target) throws AmbitException {
			try {
				engine.process(target);
			} catch (CDKException x) {
				throw new AmbitException(x);
			}
		return engine.getDescriptorSpecifications();
	}
 

}


