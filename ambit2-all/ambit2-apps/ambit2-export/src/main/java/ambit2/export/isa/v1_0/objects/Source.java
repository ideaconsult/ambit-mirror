
package ambit2.export.isa.v1_0.objects;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * ISA source schema
 * <p>
 * JSON-schema representing a source in the ISA model. Sources are considered as the starting biological material used in a study.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "@id",
    "name",
    "characteristics"
})
public class Source {

    @JsonProperty("@id")
    public URI Id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("characteristics")
    public List<MaterialAttributeValue> characteristics = new ArrayList<MaterialAttributeValue>();

}
