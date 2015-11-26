
package ambit2.export.isa.v1_0.objects;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * ISA process or protocol application schema, corresponds to 'Protocol REF' columns in the study and assay files
 * <p>
 * JSON-schema representing a protocol application in the ISA model
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "name",
    "executesProtocol",
    "parameters",
    "inputs",
    "outputs"
})
public class Process {

    @JsonProperty("name")
    public String name;
    /**
     * ISA protocol schema
     * <p>
     * JSON-schema representing a protocol in the ISA model
     * 
     */
    @JsonProperty("executesProtocol")
    public Protocol executesProtocol;
    @JsonProperty("parameters")
    public List<Parameter> parameters = new ArrayList<Parameter>();
    @JsonProperty("inputs")
    public List<Object> inputs = new ArrayList<Object>();
    @JsonProperty("outputs")
    public List<Object> outputs = new ArrayList<Object>();

}
