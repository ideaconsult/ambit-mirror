/* IteratingFolderReader.java
 * Author: nina
 * Date: Mar 14, 2009
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

package ambit2.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

/*
 * reader for multiple files in a folder
 */
public abstract class IteratingFolderReader<T, ItemReader extends IIteratingChemObjectReader> extends DefaultIteratingChemObjectReader  {
	protected static Logger logger = Logger.getLogger(IteratingFolderReader.class.getName());
	protected File[] files;
	protected int index = -1;
	protected ItemReader reader;		

	public IteratingFolderReader(File[] files) {
		super();
		setFiles(files);
	}		
	public IResourceFormat getFormat() {
		return null;
	}


	public void close() throws IOException {
		closeItemReader(reader);

	}

	protected abstract ItemReader getItemReader(int index) throws Exception ;
	protected void closeItemReader(ItemReader itemReader) throws IOException {
		if (itemReader != null) {
			itemReader.close();
			itemReader = null;
		}
	}

	public Object next() {
		if (reader != null) {
			return reader.next();
		} else return null;
	}	
	public boolean hasNext() {
		if ((files!= null) && (files.length>0) ) {
			
			if (reader != null) {
				boolean next  = reader.hasNext();
				if (next) return next;
			}
			while (true) try {
				closeItemReader(reader);
				reader = getNextReader();
				if (reader == null) return false;
				else if (reader.hasNext()) return true;
			} catch (Exception x) {
				try {handleError(x.getMessage(), x);} catch (CDKException xx) {
					logger.log(Level.SEVERE,xx.getMessage(),xx);
				} 
			}
			
		} else return false;
	}
	
	
	protected ItemReader getNextReader() throws Exception {
		index++;
		if (index < files.length) 
			return getItemReader(index);
		else return null;		
	}
	public File[] getFiles() {
		return files;
	}
	public void setFiles(File[] files) {
		this.files = files;
		if ((files!= null) && (files.length>0) )
			index = -1;
		else index = -1;
		reader = null;		
	}
	
	@Override
	public String toString() {
		return "Reads set of files";
	}	
	public void setReader(Reader reader) throws CDKException {
		throw new CDKException("Not implemented");
		
	}
	public void setReader(InputStream reader) throws CDKException {
		throw new CDKException("Not implemented");
		
	}
}
