
package ambit2.export.isa.v1_0.objects;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * ISA process parameter value schema
 * <p>
 * JSON-schema representing a ProtocolParameter Value (associated with a Protocol REF) in the ISA model
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "category",
    "value",
    "unit"
})
public class ProcessParameterValue {

    /**
     * ISA protocol parameter schema
     * <p>
     * JSON-schema representing a parameter for a protocol (category declared in the investigation file) in the ISA model
     * 
     */
    @JsonProperty("category")
    public ProtocolParameter category;
    @JsonProperty("value")
    public Object value;
    /**
     * ISA ontology reference schema
     * <p>
     * JSON-schema representing an ontology reference or annotation in the ISA model (for fields that are required to be ontology annotations)
     * 
     */
    @JsonProperty("unit")
    public OntologyAnnotation unit;

}
