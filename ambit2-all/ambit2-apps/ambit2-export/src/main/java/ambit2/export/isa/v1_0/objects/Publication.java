
package ambit2.export.isa.v1_0.objects;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * ISA investigation schema
 * <p>
 * JSON-schema representing an investigation in the ISA model
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "pubMedID",
    "doi",
    "authorList",
    "title",
    "status"
})
public class Publication {

    @JsonProperty("pubMedID")
    public String pubMedID;
    @JsonProperty("doi")
    public String doi;
    @JsonProperty("authorList")
    public String authorList;
    @JsonProperty("title")
    public String title;
    /**
     * ISA ontology reference schema
     * <p>
     * JSON-schema representing an ontology reference or annotation in the ISA model (for fields that are required to be ontology annotations)
     * 
     */
    @JsonProperty("status")
    public MeasurementType status;

}
