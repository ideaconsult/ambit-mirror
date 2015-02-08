package ambit2.rest.propertyvalue;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.PropertyValue;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.structure.CompoundResource;

public class PropertyValueURIReporter<T,Q extends IQueryRetrieval<T>> extends QueryURIReporter<T, Q> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3648376868814044783L;
	protected IStructureRecord record;
	
	public IStructureRecord getRecord() {
		return record;
	}
	public void setRecord(IStructureRecord record) {
		this.record = record;
	}
	public PropertyValueURIReporter(Request baseRef) {
		super(baseRef);
	}
	public PropertyValueURIReporter() {
	}	

	@Override
	public String getURI(String ref, T item) {
		return generateURI(ref, item);
	}
	
	@Override
	public String getURI(T item) {
		String ref = baseReference==null?"":baseReference.toString();
		if (ref.endsWith("/")) ref = ref.substring(0,ref.length()-1);	
		return getURI(ref,item);
	}
	public String generateURI(String ref, Object item) {
		if (item instanceof Property)
			return String.format("%s%s/%d%s/%s\n",
						ref,
						CompoundResource.compound,
						getRecord().getIdchemical(),
						PropertyValueResource.featureKey,
						((PropertyValue)item).getProperty().getName()
						);
		else if (item instanceof PropertyValue)
			return String.format("%s%s%s/%d%s/%d",
					ref,
					PropertyValueResource.featureKey,
					CompoundResource.compound,
					getRecord()==null?null:getRecord().getIdchemical(),
					PropertyResource.featuredef,
					((PropertyValue)item).getProperty().getId());
		return item.toString();
	}	

}	