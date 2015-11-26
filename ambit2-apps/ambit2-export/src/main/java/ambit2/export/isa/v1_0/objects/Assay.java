
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
    "fileName",
    "measurementType",
    "technologyType",
    "technologyPlatform",
    "processSequence"
})
public class Assay {

    @JsonProperty("fileName")
    public String fileName;
    @JsonProperty("measurementType")
    public MeasurementType measurementType;
    @JsonProperty("technologyType")
    public TechnologyType technologyType;
    @JsonProperty("technologyPlatform")
    public String technologyPlatform;
    @JsonProperty("processSequence")
    public List<Process> processSequence = new ArrayList<Process>();

}
