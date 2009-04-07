/* QuerySimilarityStructure.java
 * Author: nina
 * Date: Apr 7, 2009
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

package ambit2.db.search.structure;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;

import ambit2.base.exceptions.AmbitException;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryParam;

public class QuerySimilarityStructure extends QuerySimilarity<IMoleculeSet,NumberCondition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7669825969508301397L;
	protected QuerySimilarityBitset bitsetSimilarity;
	protected FingerprintGenerator g;

	public QuerySimilarityStructure() {
		super();
		bitsetSimilarity = new QuerySimilarityBitset();
		g = new FingerprintGenerator();
		setCondition(NumberCondition.getInstance(">="));	
		setThreshold(0.75);		
	}
	@Override
	public void setForceOrdering(boolean forceOrdering) {
		bitsetSimilarity.setForceOrdering(forceOrdering);
	}
	
	@Override
	public boolean isForceOrdering() {
		return bitsetSimilarity.isForceOrdering();
	}
	public List<QueryParam> getParameters() throws AmbitException {
		return bitsetSimilarity.getParameters();
	}
	@Override
	public void setThreshold(Double threshold) {
		bitsetSimilarity.setThreshold(threshold);
	}
	@Override
	public double getThreshold() {
		return bitsetSimilarity.getThreshold();
	}
	@Override
	public void setCondition(NumberCondition condition) {
		bitsetSimilarity.setCondition(condition);
	}
	@Override
	public NumberCondition getCondition() {
		return bitsetSimilarity.getCondition();
	}
	public String getSQL() throws AmbitException {
		return bitsetSimilarity.getSQL();
	}
	public void setStructure(IAtomContainer structure) {
		if (getValue()==null) setValue(NoNotificationChemObjectBuilder.getInstance().newMoleculeSet());
		else getValue().removeAllAtomContainers();
		getValue().addAtomContainer(structure);
		setValue(getValue());
	}
	
	@Override
	public void setValue(IMoleculeSet set) {
		super.setValue(set);
		try {
			Iterator<IAtomContainer> i = set.molecules();
			BitSet bitset = new BitSet();
			while (i.hasNext()) {
				bitset.or(g.process(i.next()));
			}
			bitsetSimilarity.setValue(bitset);
		} catch (Exception x) {
			bitsetSimilarity.setValue(null);
		}
	}
	@Override
	public void setId(Integer id) {
		super.setId(id);
		bitsetSimilarity.setId(id);
	}
	@Override
	public Integer getId() {
		return bitsetSimilarity.getId();
	}
	@Override
	public String getName() {
		return bitsetSimilarity.getName();
	}
	@Override
	public void setName(String name) {
		bitsetSimilarity.setName(name);
	}
}
