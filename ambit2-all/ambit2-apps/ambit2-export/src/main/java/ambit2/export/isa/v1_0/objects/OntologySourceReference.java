
package ambit2.export.isa.v1_0.objects;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * ISA ontology source reference schema
 * <p>
 * JSON-schema representing an ontology reference in the ISA model
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "description",
    "file",
    "name",
    "version"
})
public class OntologySourceReference {

    @JsonProperty("description")
    public String description;
    @JsonProperty("file")
    public String file;
    @JsonProperty("name")
    public String name;
    @JsonProperty("version")
    public String version;

}
