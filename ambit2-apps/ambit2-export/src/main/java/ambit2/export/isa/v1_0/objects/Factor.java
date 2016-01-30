
package ambit2.export.isa.v1_0.objects;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * ISA factor schema
 * <p>
 * JSON-schema representing a factor value in the ISA model
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "@id",
    "factorName",
    "factorType",
    "comments"
})
public class Factor {

    @JsonProperty("@id")
    public URI Id;
    @JsonProperty("factorName")
    public String factorName;
    /**
     * ISA ontology reference schema
     * <p>
     * JSON-schema representing an ontology reference or annotation in the ISA model (for fields that are required to be ontology annotations)
     * 
     */
    @JsonProperty("factorType")
    public OntologyAnnotation factorType;
    @JsonProperty("comments")
    public List<Comment> comments = new ArrayList<Comment>();

}
