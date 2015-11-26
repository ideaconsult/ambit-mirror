
package ambit2.export.isa.v1_0.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Study JSON Schema
 * <p>
 * JSON Schema describing an Study
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "identifier",
    "title",
    "description",
    "submissionDate",
    "publicReleaseDate",
    "publications",
    "people",
    "studyDesignDescriptors",
    "protocols",
    "sources",
    "samples",
    "processSequence",
    "assays"
})
public class Study {

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
    @JsonProperty("publications")
    public List<Publication> publications = new ArrayList<Publication>();
    @JsonProperty("people")
    public List<Person> people = new ArrayList<Person>();
    @JsonProperty("studyDesignDescriptors")
    public List<OntologyAnnotation> studyDesignDescriptors = new ArrayList<OntologyAnnotation>();
    @JsonProperty("protocols")
    public List<Protocol> protocols = new ArrayList<Protocol>();
    @JsonProperty("sources")
    public List<Source> sources = new ArrayList<Source>();
    @JsonProperty("samples")
    public List<Sample> samples = new ArrayList<Sample>();
    @JsonProperty("processSequence")
    public List<Process> processSequence = new ArrayList<Process>();
    @JsonProperty("assays")
    public List<Assay> assays = new ArrayList<Assay>();

}
