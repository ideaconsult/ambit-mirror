
package ambit2.export.isa.v1_0.objects;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * ISA material attribute schema
 * <p>
 * JSON-schema representing a material attribute or characteristic in the ISA model
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "value",
    "unit"
})
public class MaterialAttribute {

    @JsonProperty("value")
    public Object value;
    /**
     * ISA ontology reference schema
     * <p>
     * JSON-schema representing an ontology reference or annotation in the ISA model (for fields that are required to be ontology annotations)
     * 
     */
    @JsonProperty("unit")
    public MeasurementType unit;

}
