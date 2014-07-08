package ambit2.pubchem.rest;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.search.AbstractFinder;
import ambit2.pubchem.rest.PUGRestRequest.COMPOUND_DOMAIN_INPUT;

public class PubChemRestFinder extends AbstractFinder<PUGRestCompoundRequest, IStructureRecord> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1681833725212826604L;
	public PubChemRestFinder(Template profile,AbstractFinder.MODE mode,COMPOUND_DOMAIN_INPUT input) {
		super(profile,new PUGRestCompoundRequest(input),mode);
	}
	protected IStructureRecord query(String term) throws AmbitException {
		List<IStructureRecord> records = request.process(term);
		return records==null?null:records.size()==0?null:records.get(0);
	};
	@Override
	protected Object retrieveValue(IStructureRecord target, Property key)
			throws AmbitException {
		Object value = super.retrieveValue(target, key);
		switch (request.input) {
		case cid: {
			if (value instanceof Number) 
				return ((Number) value).longValue();
		}
		}
		return value;
	}
}
