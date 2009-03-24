package ambit2.db.processors;

import java.util.BitSet;

import org.openscience.cdk.smiles.SmilesGenerator;

import ambit2.base.exceptions.AmbitException;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.search.EQCondition;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.QueryCombined;
import ambit2.db.search.QueryDataset;
import ambit2.db.search.QueryField;
import ambit2.db.search.QueryInfo;
import ambit2.db.search.QueryPrescreenBitSet;
import ambit2.db.search.QuerySimilarityBitset;
import ambit2.db.search.QueryStored;
import ambit2.db.search.QueryStructure;
import ambit2.db.search.StringCondition;

public class QueryInfo2Query extends AbstractDBProcessor<QueryInfo,IQueryObject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2333531372300024401L;

	public IQueryObject process(QueryInfo target) throws AmbitException {
		QueryCombined combined = new QueryCombined();
		//scope
		if (QueryInfo.SCOPE_DATABASE.equals(target.getScope()))
			combined.setScope(null);
		else if (QueryInfo.SCOPE_DATASET.equals(target.getScope())) {
			if (target.getDataset() != null) {
				QueryDataset d = new QueryDataset();
				d.setCondition(EQCondition.getInstance());
				d.setValue(target.getDataset());
				combined.setScope(d);
			} else {
				logger.warn("Dataset not defined!");
				combined.setScope(null);
			}
		} else if (QueryInfo.SCOPE_QUERY.equals(target.getScope())) {
			if (target.getStoredQuery() != null) {
				QueryStored d = new QueryStored();
				d.setCondition(EQCondition.getInstance());
				d.setValue(target.getStoredQuery());
				combined.setScope(d);
			} else {
				logger.warn("Stored query not defined!");
				combined.setScope(null);
			}
		}
		//and / or
		combined.setCombine_as_and(QueryInfo.COMBINE_ALL.equals(target.getCombine()));
		
		//identifiers
		QueryField 
			f = createQueryField(target.getFieldname1(), target.getIdentifier1(), target.getCondition1());
		if (f != null) combined.add(f);
			f = createQueryField(target.getFieldname2(), target.getIdentifier2(), target.getCondition2());
		if (f != null) combined.add(f);
			f = createQueryField(target.getFieldname3(), target.getIdentifier3(), target.getCondition3());
		if (f != null) combined.add(f);		
		
		//formula, smiles, inchi
		QueryStructure 
			s = createQueryStructure("formula",target.getFormula());
		if (s != null) combined.add(f);
			s = createQueryStructure("smiles",target.getSmiles());
		if (s != null) combined.add(f);
			s = createQueryStructure("inchi",target.getInchi());
		if (s != null) combined.add(f);			
		
		//similarity
		try {
			QuerySimilarityBitset similarity = createQuerySimilarity(target);
			if (similarity != null)
				combined.add(similarity);
		} catch (AmbitException x) {
			logger.warn(x);
		}
		
		//substructure
		try {
			QueryPrescreenBitSet substructure = createQuerySubstructure(target);
			if (substructure != null)
				combined.add(substructure);
		} catch (AmbitException x) {
			logger.warn(x);
		}	
		
		//substructure
		try {
			QueryStructure exact = createExactStructure(target);
			if (exact != null)
				combined.add(exact);
		} catch (AmbitException x) {
			logger.warn(x);
		}				
		combined.setId(-1);
		return combined;
	}
	protected QueryStructure createExactStructure(QueryInfo target) throws AmbitException {
		if (QueryInfo.METHOD_EXACT.equals(target.getMethod())) {
			if (target.getMolecule() == null) return null;
			SmilesGenerator g = new SmilesGenerator();
			return createQueryStructure("smiles", g.createSMILES(target.getMolecule()));
		} else return null;		
	}
	
	protected QueryPrescreenBitSet createQuerySubstructure(QueryInfo target) throws AmbitException {
		if (QueryInfo.METHOD_SUBSTRUCTURE.equals(target.getMethod())) {
			if (target.getMolecule() == null) return null;
			FingerprintGenerator g = new FingerprintGenerator();
			BitSet bitset = g.process(target.getMolecule());
			QueryPrescreenBitSet qs = new QueryPrescreenBitSet();
			qs.setBitset(bitset);
			return qs;
		} else return null;		
	}	
	protected QuerySimilarityBitset createQuerySimilarity(QueryInfo target) throws AmbitException {
		if (QueryInfo.METHOD_SIMILARITY.equals(target.getMethod())) {
			if (target.getMolecule() == null) return null;
			FingerprintGenerator g = new FingerprintGenerator();
			BitSet bitset = g.process(target.getMolecule());
			QuerySimilarityBitset qs = new QuerySimilarityBitset();
			qs.setBitset(bitset);
			qs.setThreshold(target.getThreshold());
			return qs;
		} else return null;		
	}
	protected QueryStructure createQueryStructure(String field, String value) {
		if (value == null) return null;
		if ("".equals(value.trim())) return null;
		if (field == null) return null;
		if ("".equals(field.trim())) return null;
		
		QueryStructure f = new QueryStructure();
		f.setFieldname(field);
		f.setValue(value);
		return f;
	}	

	protected QueryField createQueryField(String field, String value, String condition) {
		if (value == null) return null;
		if ("".equals(value.trim())) return null;
		if (field == null) field = "";
		
		QueryField f = new QueryField();
		if (condition == null)
			f.setCondition(StringCondition.getInstance(StringCondition.C_EQ));
		else
			f.setCondition(StringCondition.getInstance(condition));
		f.setFieldname(field);
		f.setValue(value);
		return f;
	}
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
}
