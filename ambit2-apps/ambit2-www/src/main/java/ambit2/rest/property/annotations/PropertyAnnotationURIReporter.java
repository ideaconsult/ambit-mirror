package ambit2.rest.property.annotations;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.PropertyAnnotation;
import ambit2.rest.property.PropertyResource;

/**
 * Generates uri of {@link PropertyResource}
 * @author nina
 *
 */
public class PropertyAnnotationURIReporter extends QueryURIReporter<PropertyAnnotation, IQueryRetrieval<PropertyAnnotation>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 711954935147788056L;
	protected boolean propertyOnly = false;
	public boolean isPropertyOnly() {
		return propertyOnly;
	}
	public void setPropertyOnly(boolean propertyOnly) {
		this.propertyOnly = propertyOnly;
	}
	public PropertyAnnotationURIReporter(Reference baseRef) {
		super(baseRef);
	}
	public PropertyAnnotationURIReporter(Request ref) {
		super(ref);
	}
	public PropertyAnnotationURIReporter() {
		this((Request)null);
	}
	@Override
	public String getURI(String ref, PropertyAnnotation record) {

		if (record.getIdproperty()>0)
			return String.format("%s%s/%d%s",ref,PropertyResource.featuredef,record.getIdproperty(),
					isPropertyOnly()?"":PropertyAnnotationResource.annotation
					);
		else
			return null;

	}

}
