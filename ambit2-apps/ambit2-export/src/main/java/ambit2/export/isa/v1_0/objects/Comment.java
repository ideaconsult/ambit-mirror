
package ambit2.export.isa.v1_0.objects;

import java.net.URI;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * ISA comment schema - it corresponds to ISA Comment[] construct
 * <p>
 * JSON-schema representing a comment in the ISA model
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "@id",
    "name",
    "value"
})
public class Comment {

    @JsonProperty("@id")
    public URI Id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("value")
    public String value;

}
