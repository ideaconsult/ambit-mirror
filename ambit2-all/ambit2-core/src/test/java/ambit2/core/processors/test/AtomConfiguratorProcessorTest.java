/* AtomConfiguratorProcessor.java
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

package ambit2.core.processors.test;


import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.core.processors.structure.AtomConfigurator;

public class AtomConfiguratorProcessorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	/**
	 * Atom configurator sets number of properties,which are otherwise uninitialized
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		AtomConfigurator p = new AtomConfigurator();
		IAtomContainer mol = MoleculeFactory.makeBenzene();
		int uninitialized = 0;
		for (int i=0; i < mol.getAtomCount(); i++) {
			if(mol.getAtom(i).getAtomicNumber()==null || mol.getAtom(i).getHybridization()==null)
				uninitialized++;
		}
		if (uninitialized>0) {
			mol = p.process(mol);
			for (int i=0; i < mol.getAtomCount(); i++) {
				Assert.assertEquals(6,mol.getAtom(i).getAtomicNumber().intValue());
				Assert.assertEquals(Hybridization.SP2,mol.getAtom(i).getHybridization());
				Assert.assertNotNull(mol.getAtom(i).getValency());
			}
		}
	}
}
