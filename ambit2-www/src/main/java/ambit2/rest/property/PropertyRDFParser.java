package ambit2.rest.property;

import com.hp.hpl.jena.ontology.OntModel;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.rest.AbstractRDFParser;

/**
 * Creates {@link Property} from {@link OntModel}
 * @author nina
 *
 */
public class PropertyRDFParser extends AbstractRDFParser<Property> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3860276644893408902L;

	@Override
	public Property read(OntModel model) throws AmbitException {
		// TODO Auto-generated method stub
		return null;
	}

}
