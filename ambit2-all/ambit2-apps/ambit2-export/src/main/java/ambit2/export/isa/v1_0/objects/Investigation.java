
package ambit2.export.isa.v1_0.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    "filename",
    "identifier",
    "title",
    "description",
    "submissionDate",
    "publicReleaseDate",
    "commentCreatedWithConfiguration",
    "commentLastOpenedWithConfiguration",
    "ontologySourceReferences",
    "publications",
    "people",
    "studies"
})
public class Investigation {

    @JsonProperty("filename")
    public String filename;
    @JsonProperty("identifier")
    public String identifier;
    @JsonProperty("title")
    public String title;
    @JsonProperty("description")
    public String description;
    @JsonProperty("submissionDate")
    public Date submissionDate;
    @JsonProperty("publicReleaseDate")
    public Date publicReleaseDate;
    /**
     * ISA comment schema - it corresponds to ISA Comment[] construct
     * <p>
     * JSON-schema representing a data file in the ISA model
     * 
     */
    @JsonProperty("commentCreatedWithConfiguration")
    public Comment commentCreatedWithConfiguration;
    /**
     * ISA comment schema - it corresponds to ISA Comment[] construct
     * <p>
     * JSON-schema representing a data file in the ISA model
     * 
     */
    @JsonProperty("commentLastOpenedWithConfiguration")
    public Comment commentLastOpenedWithConfiguration;
    @JsonProperty("ontologySourceReferences")
    public List<OntologySourceReference> ontologySourceReferences = new ArrayList<OntologySourceReference>();
    @JsonProperty("publications")
    public List<Publication> publications = new ArrayList<Publication>();
    @JsonProperty("people")
    public List<Person> people = new ArrayList<Person>();
    @JsonProperty("studies")
    public List<Study> studies = new ArrayList<Study>();

}
