package ambit2.rest.propertyvalue;

import ambit2.rest.DataResources;
import ambit2.rest.property.PropertyResource;

/**

 * @author nina
 *
 */
public class FeatureResourceIdentifiers  {
	public final static String CompoundFeaturedefID = String.format("%s%s%s/{%s}",
			PropertyValueResource.featureKey,DataResources.compoundID_resource,PropertyResource.featuredef,PropertyResource.idfeaturedef);
	public final static String ConformerFeaturedefID = String.format("%s%s%s/{%s}",
			PropertyValueResource.featureKey,DataResources.conformerID_resource,PropertyResource.featuredef,PropertyResource.idfeaturedef);

	public enum headers  {
			value;
			public boolean isMandatory() {
				return false;
			}			
			public String getDescription() {
				return toString();
			}
	};		
	public static final String featureID = "idfeature";
	public static final String resource = String.format("%s/{%s}",PropertyValueResource.featureKey,featureID);

}
