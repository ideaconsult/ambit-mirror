
package ambit2.export.isa.v1_0.objects;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Assay JSON Schema
 * <p>
 * JSON Schema describing an Assay
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "filename",
    "measurementType",
    "technologyType",
    "technologyPlatform",
    "processSequence"
})
public class Assay {

    @JsonProperty("filename")
    public String filename;
    /**
     * ISA ontology reference schema
     * <p>
     * JSON-schema representing an ontology reference or annotation in the ISA model (for fields that are required to be ontology annotations)
     * 
     */
    @JsonProperty("measurementType")
    public MeasurementType measurementType;
    @JsonProperty("technologyType")
    public TechnologyType technologyType;
    @JsonProperty("technologyPlatform")
    public String technologyPlatform;
    @JsonProperty("processSequence")
    public List<Process> processSequence = new ArrayList<Process>();

}
