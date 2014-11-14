/* ProcessorFile2FileReader.java
 * Author: Nina Jeliazkova
 * Date: Apr 13, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
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

package ambit2.db.processors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.base.processors.ProcessorException;
import ambit2.core.io.RawIteratingSDFReader;

/**
 * 
 * Returns {@link IIteratingChemObjectReader} based on {@link File}.
 * TODO extend for different types; move code from FileState here
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Apr 13, 2008
 */
public class ProcessorFile2FileReader extends DefaultAmbitProcessor<File,IIteratingChemObjectReader> {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6446046376999783460L;

	public IIteratingChemObjectReader process(File target) throws AmbitException {
        try {
            return new RawIteratingSDFReader(new FileReader(target));
        } catch (CDKException x) {
        	throw new ProcessorException(this,x);
        } catch (FileNotFoundException x) {
            throw new ProcessorException(this,x);
        }

    }
        
}
