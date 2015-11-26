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
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.base.data.ClassHolder;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.MoleculeTools;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.search.NumberCondition;

public class QuerySimilarityStructure extends QuerySimilarity<ClassHolder,IAtomContainerSet,NumberCondition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7669825969508301397L;
	protected AbstractStructureQuery query;
	protected FingerprintGenerator g;
	protected MoleculeReader molReader = null;
	protected UniversalIsomorphismTester uit = new UniversalIsomorphismTester();
	public static ClassHolder[] methods = new ClassHolder[] {
			new ClassHolder("ambit2.db.search.structure.QuerySimilarityBitset","Tanimoto [fingerprints]","",""),
			new ClassHolder("ambit2.db.search.structure.QueryExactStructure","Exact structure","",""),
			
	};

	public QuerySimilarityStructure() {
		super();
		query = new QuerySimilarityBitset();
		g = new FingerprintGenerator(new Fingerprinter());
		setCondition(NumberCondition.getInstance(">="));	
		setThreshold(0.75);		
	}
	@Override
	public void setFieldname(ClassHolder method) {
		try {
			Class classDefinition = getClass().getClassLoader().loadClass(method.getClazz());
			Object o = classDefinition.newInstance();
			if (o instanceof QuerySimilarityBitset) {
				QuerySimilarityBitset query = (QuerySimilarityBitset)o;
				query.setCondition(getCondition());
				query.setThreshold(getThreshold());
				query.setForceOrdering(isForceOrdering());
				this.query  = query;
				setValue(getValue());
				super.setFieldname(method);				
			} else if (o instanceof QueryExactStructure) {
				QueryExactStructure query = (QueryExactStructure)o;
				
				this.query  = query;
				setValue(getValue());
				if ((getValue()!=null) && (getValue().getAtomContainerCount()>0))
					query.setValue(getValue().getAtomContainer(0));
				super.setFieldname(method);						
			}
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
	}
	@Override
	public void setForceOrdering(boolean forceOrdering) {
		if (query instanceof QuerySimilarityBitset)
			((QuerySimilarityBitset)query).setForceOrdering(forceOrdering);
	}
	
	@Override
	public boolean isForceOrdering() {
		if (query instanceof QuerySimilarityBitset)
			return ((QuerySimilarityBitset)query).isForceOrdering();
		return false;
	}
	public List<QueryParam> getParameters() throws AmbitException {
		return query.getParameters();
	}
	
	@Override
	public void setThreshold(double threshold) {
		super.setThreshold(threshold);
		if (query instanceof QuerySimilarityBitset)
			((QuerySimilarityBitset)query).setThreshold(threshold);
	}
	@Override
	public double getThreshold() {
		if (query instanceof QuerySimilarityBitset)
			return ((QuerySimilarityBitset)query).getThreshold();
		return super.getThreshold();		
	}
	
	@Override
	public void setCondition(NumberCondition condition) {
		query.setCondition(condition);
	}
	@Override
	public NumberCondition getCondition() {
		if (query.getCondition() instanceof NumberCondition)
			return (NumberCondition)query.getCondition();		
		else return NumberCondition.getInstance("=");
	}
	public String getSQL() throws AmbitException {
		return query.getSQL();
	}
	public void setStructure(IAtomContainer structure) {
		if (getValue()==null) setValue(MoleculeTools.newMoleculeSet(SilentChemObjectBuilder.getInstance()));
		else getValue().removeAllAtomContainers();
		getValue().addAtomContainer(structure);
		setValue(getValue());
	}
	
	@Override
	public void setValue(IAtomContainerSet set) {
		super.setValue(set);
		try {
			//if (query instanceof QuerySimilarityBitset) {
				Iterator<IAtomContainer> i = set.atomContainers().iterator();
				BitSet bitset = new BitSet();
				while (i.hasNext()) {
					bitset.or(g.process(i.next()));
				}
				query.setValue(bitset);
			//} else
			//	query.setValue(set);
		} catch (Exception x) {
			query.setValue(null);
		}
	}
	@Override
	public void setId(Integer id) {
		super.setId(id);
		query.setId(id);
	}
	@Override
	public Integer getId() {
		return query.getId();
	}
	@Override
	public String getName() {
		return query.getName();
	}
	@Override
	public void setName(String name) {
		query.setName(name);
	}
	@Override
	public String toString() {
		if (getFieldname() == null) return "Search for similar structures/substructures";
		else return query.toString();
	}
	@Override
	public boolean isPrescreen() {
		if (query==null) return super.isPrescreen();
		else return query.isPrescreen();
	}
	@Override
	public double calculateMetric(IStructureRecord object) {
		try {
			if (query==null) return super.calculateMetric(object);
			else if (query instanceof QueryPrescreenBitSet) {
				if (molReader == null) molReader = new MoleculeReader();
				IAtomContainer target = molReader.process(object);
				int match = 0;
				for (int i=0; i < getValue().getAtomContainerCount();i++) {
					IAtomContainer q = getValue().getAtomContainer(i);
					if (q.getAtomCount()==0) continue;
					if (!uit.isSubgraph(target, q)) {
						return 0;
					} else match++;
				}
				return match;
			} else return 1;
		} catch (Exception x) {
			return -1;
		}
	}
}
