package ambit2.chebi;

import net.idea.modbcum.i.exceptions.AmbitException;
import uk.ac.ebi.chebi.webapps.chebiWS.model.ChebiWebServiceFault_Exception;
import uk.ac.ebi.chebi.webapps.chebiWS.model.SearchCategory;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.search.AbstractFinder;

public class ChebiFinder extends AbstractFinder<ChEBIClient, IStructureRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3706639378344745115L;

	public ChebiFinder(Template profile,  MODE mode) {
		super(profile, null, mode);
	}

	@Override
	protected IStructureRecord query(String term) throws AmbitException {
		try {
			ChEBIClient client = new ChEBIClient(term,SearchCategory.ALL);
			while (client.hasNext()) {
				IStructureRecord record = client.next();
				return record;
			}
			return null;
		} catch (ChebiWebServiceFault_Exception x) {
			throw new AmbitException(x);
		}
	}

}
