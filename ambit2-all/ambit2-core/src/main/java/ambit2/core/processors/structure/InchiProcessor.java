/* InchiGenerator.java
 * Author: nina
 * Date: Jan 11, 2009
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

package ambit2.core.processors.structure;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.sf.jniinchi.INCHI_OPTION;
import net.sf.jniinchi.INCHI_RET;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;

public class InchiProcessor extends DefaultAmbitProcessor<IAtomContainer, InChIGenerator> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9052892261511104974L;
	protected InChIGeneratorFactory factory;
	
	public InchiProcessor() throws CDKException {
		super();
		// Generate factory - throws CDKException if native code does not load
		factory = InChIGeneratorFactory.getInstance();
	}
	public InChIGenerator process(IAtomContainer target) throws AmbitException {
		try {
			// Get InChIGenerator

			List<net.sf.jniinchi.INCHI_OPTION> options = new ArrayList<net.sf.jniinchi.INCHI_OPTION>();
			/*
			options.add(INCHI_OPTION.ChiralFlagON);
			options.add(INCHI_OPTION.SUCF);
			options.add(INCHI_OPTION.SRel);
			*/
			
			options.add(INCHI_OPTION.SAbs);
			options.add(INCHI_OPTION.SAsXYZ);
			options.add(INCHI_OPTION.SPXYZ);
			options.add(INCHI_OPTION.FixSp3Bug);
			InChIGenerator gen = factory.getInChIGenerator(target,options);

			INCHI_RET ret = gen.getReturnStatus();
			if (ret == INCHI_RET.WARNING) {
				// InChI generated, but with warning message
				logger.warning("InChI warning: " + gen.getMessage());
			} else if (ret != INCHI_RET.OKAY) {
				// InChI generation failed
				throw new AmbitException("InChI failed: " + ret.toString()
				+ " [" + gen.getMessage() + "]");
			}

			return gen;
			
		} catch (CDKException x) {
			throw new AmbitException(x);
		}
	}

}
