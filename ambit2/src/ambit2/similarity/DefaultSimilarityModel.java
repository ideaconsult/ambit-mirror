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

package ambit2.similarity;

import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.qsar.model.QSARModelException;

import ambit2.exceptions.AmbitException;

public class DefaultSimilarityModel implements ISimilarityModel {
	protected IIteratingChemObjectReader reader;
	protected ISimilarityProcessor similarityProcessor;
	public DefaultSimilarityModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void build() throws QSARModelException {
		try {
			similarityProcessor.buildInitialize();
			IIteratingChemObjectReader reader = getReader();
			while (reader.hasNext()) {
				try {
					similarityProcessor.incrementalBuild(reader.next());
				} catch (AmbitException x) {
					x.printStackTrace();
					//TODO logger?
				}
			}
			similarityProcessor.buildCompleted();
		} catch (AmbitException x) {
			x.printStackTrace();
		}
	}

	public void predict() throws QSARModelException {
		if (similarityProcessor.isPredicting())
			while (reader.hasNext()) {
				try {
					similarityProcessor.predict(reader.next());
				} catch (AmbitException x) {
					x.printStackTrace();
					//TODO logger?
				}
			}
		else throw new QSARModelException("Model not built for "+similarityProcessor.toString());
	}

	public ISimilarityProcessor getSimilarityProcessor() {
		return similarityProcessor;
	}

	public void setSimilarityProcessor(ISimilarityProcessor similarityProcessor) {
		this.similarityProcessor = similarityProcessor;
	}

	public IIteratingChemObjectReader getReader() {
		return reader;
	}

	public void setReader(IIteratingChemObjectReader reader) {
		this.reader = reader;
	}

}


