/* EntrezSearchProcessor.java
 * Author: nina
 * Date: Mar 20, 2009
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

package ambit2.core.pubchem;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

import ambit2.core.data.IStructureRecord;
import ambit2.core.exceptions.AmbitException;
import ambit2.core.exceptions.NotFoundException;
import ambit2.core.processors.ProcessorException;


public class EntrezSearchProcessor extends HTTPRequest<String, List<IStructureRecord>> {
	protected String entrezURL = "http://www.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pccompound&maxret=100&term=";
	protected final EntrezESearchParser parser = new EntrezESearchParser();
	protected final PUGProcessor pug = new PUGProcessor();
	/**
	 * 
	 */
	private static final long serialVersionUID = -8215370335359483817L;

	@Override
	protected List<IStructureRecord> parseInput(String target, InputStream in)
			throws ProcessorException {
		try {
			 return parser.process(in);
		} catch (AmbitException x) {
			throw new ProcessorException(this,x);
		}
	}
	@Override
	protected void prepareOutput(String target, OutputStream out)
			throws ProcessorException {
		
	}
	public List<IStructureRecord>  process(String target) throws AmbitException {
		try {
			setUrl(entrezURL+URLEncoder.encode(target, "US-ASCII"));
			System.out.println(getUrl());
			List<IStructureRecord> records = super.process(target);
			if ((records == null) || (records.size()==0))
				throw new NotFoundException(target);
			for (int i=records.size()-1;i>=0; i--)
				if (!records.get(i).getFormat().equals(PUGProcessor.PUBCHEM_CID))
					throw new ProcessorException(this,records.get(i).getFormat()+records.get(i).getContent());
			return pug.process(records);
		} catch (Exception x) {
			throw new ProcessorException(this,x);
		}

	}

	
}
