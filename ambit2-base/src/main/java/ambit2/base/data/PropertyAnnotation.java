package ambit2.base.data;

import java.io.Serializable;

/**
 * Additional triples for {@link Property}, or opentox:Feature
 * @author nina
 *
 */
public class PropertyAnnotation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6469337758176216737L;
	protected int idproperty;
	
	public int getIdproperty() {
		return idproperty;
	}
	public void setIdproperty(int idproperty) {
		this.idproperty = idproperty;
	}
	protected String type;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPredicate() {
		return predicate;
	}
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	protected String predicate;
	protected String object;

	@Override
	public String toString() {
		return String.format("Type: %s Predicate %s Object %s",type,predicate,object);
	}

}
