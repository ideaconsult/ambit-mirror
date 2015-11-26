
package ambit2.export.isa.v1_0.objects;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * ISA factor value schema
 * <p>
 * JSON-schema representing a factor value in the ISA model
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "factor",
    "value"
})
public class FactorValue {

    /**
     * ISA factor schema
     * <p>
     * JSON-schema representing a factor value in the ISA model
     * 
     */
    @JsonProperty("factor")
    public Factor factor;
    @JsonProperty("value")
    public Value value;

}
