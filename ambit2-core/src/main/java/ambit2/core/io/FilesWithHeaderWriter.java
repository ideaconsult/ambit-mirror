/* FilesWithHeaderWriter.java
 * Author: Nina Jeliazkova
 * Date: Feb 2, 2008 
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

package ambit2.core.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.openscience.cdk.io.DefaultChemObjectWriter;

import ambit2.base.data.Property;

public abstract class FilesWithHeaderWriter extends DefaultChemObjectWriter {
	protected static Logger logger = Logger
			.getLogger(FilesWithHeaderWriter.class.getName());
	public static String defaultSMILESHeader = "SMILES";
	protected ArrayList header = null;
	protected boolean writingStarted = false;
	protected int smilesIndex = -1;
	protected boolean addSMILEScolumn = true;
	public boolean isAddSMILEScolumn() {
		return addSMILEScolumn;
	}

	public void setAddSMILEScolumn(boolean addSMILEScolumn) {
		this.addSMILEScolumn = addSMILEScolumn;
	}

	/**
	 * @return Returns the header.
	 */
	public synchronized ArrayList getHeader() {
		return header;
	}

	/**
	 * Creates header from Hashtable keys Used for default header - created from
	 * properties of the first molecule written
	 * 
	 * @param properties
	 */
	public void setHeader(Map properties) {
		if (writingStarted) {
			logger.warning("Can't change header while writing !!!!");
			return; // cant' change header !
		}
		header = new ArrayList();
		Iterator e = properties.keySet().iterator();
		smilesIndex = -1;
		while (e.hasNext())
			header.add(e.next());

		Collections.sort(header, new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				if (o1 instanceof Property) {
					if (o2 instanceof Property) {
						return ((Property) o1).getOrder()
								- ((Property) o2).getOrder();
					} else {
						return -Integer.MAX_VALUE;
					}
				} else {
					if (o2 instanceof Property) {
						return Integer.MAX_VALUE;
					} else
						return o1.toString().compareTo(o2.toString());
				}
			}
		});
		for (int j = 0; j < header.size(); j++)
			if (header.get(j).toString().equals(defaultSMILESHeader)) {
				smilesIndex = j;
				break;
			}
		if (smilesIndex == -1 && addSMILEScolumn) {
			header.add(defaultSMILESHeader);
			smilesIndex = header.size()-1;
		}
		logger.fine("Header created from hashtable\t" + header);
	}

	abstract protected void writeHeader() throws IOException;

	/**
	 * @param header
	 *            The header to set.
	 */
	public synchronized void setHeader(ArrayList header) {
		if (writingStarted) {
			logger.warning("Can't change header while writing !!!!");
			return; // cant' change header !
		}
		this.header = header;
		smilesIndex = -1;
		for (int i = 0; i < header.size(); i++)
			if (header.get(i).toString().equals(defaultSMILESHeader))
				smilesIndex = i;
		if (smilesIndex == -1) {
			header.add(defaultSMILESHeader);
			smilesIndex = header.size()-1;
		}
		logger.fine("Header created\t" + header);
	}
}
