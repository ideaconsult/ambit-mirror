package ambit2.pubchem;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.search.AbstractFinder;

public class PubchemFinder extends AbstractFinder<EntrezSearchProcessor, IStructureRecord> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1681833725212826604L;
	public PubchemFinder(Template profile,AbstractFinder.MODE mode) {
		super(profile,new EntrezSearchProcessor(),mode);
	}
	protected IStructureRecord query(String term) throws AmbitException {
		List<IStructureRecord> records = request.process(term);
		return records==null?null:records.size()==0?null:records.get(0);
	};
}
