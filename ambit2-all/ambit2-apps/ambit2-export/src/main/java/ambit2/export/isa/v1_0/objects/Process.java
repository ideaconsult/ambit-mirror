
package ambit2.export.isa.v1_0.objects;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
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
    "@id",
    "name",
    "executesProtocol",
    "parameterValues",
    "performer",
    "date",
    "inputs",
    "outputs",
    "comments"
})
public class Process {

    @JsonProperty("@id")
    public URI Id;
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
    @JsonProperty("parameterValues")
    public List<ProcessParameterValue> parameterValues = new ArrayList<ProcessParameterValue>();
    @JsonProperty("performer")
    public String performer;
    @JsonProperty("date")
    public Date date;
    @JsonProperty("inputs")
    public List<Object> inputs = new ArrayList<Object>();
    @JsonProperty("outputs")
    public List<Object> outputs = new ArrayList<Object>();
    @JsonProperty("comments")
    public List<Comment> comments = new ArrayList<Comment>();

}
