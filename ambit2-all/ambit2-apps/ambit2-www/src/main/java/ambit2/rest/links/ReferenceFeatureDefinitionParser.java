package ambit2.rest.links;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.restlet.data.Reference;

import ambit2.base.data.Property;

/**
 * Generates property object give an Reference
 * @author nina
 *
 */
public class ReferenceFeatureDefinitionParser extends ReferenceParser<Property> {
	/*
	protected Template template = new Template(
					String.format("%s/{%s}%s",
					DatasetResource.dataset,DatasetResource.datasetKey,PropertyResource.featuredef));
					*/
	/**
	 * 
	 */
	private static final long serialVersionUID = -4429192529028878499L;

	public Property process(Reference target) throws AmbitException {
		
		int no = 0;
		for (String s : target.getSegments()) {
			if (no == 1) {
				Property p = new Property(target.toString(),null);
				p.setEnabled(true);
				p.setId(Integer.parseInt(target.getLastSegment()));
				return p;
			}
			if ("feature".equals(s)) no++;
		}
		return null;
		
	}

}
