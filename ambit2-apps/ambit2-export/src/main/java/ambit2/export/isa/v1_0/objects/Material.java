
package ambit2.export.isa.v1_0.objects;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * ISA material node schema
 * <p>
 * JSON-schema representing a material node in the ISA model
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "name",
    "characteristics"
})
public class Material {

    @JsonProperty("name")
    public String name;
    @JsonProperty("characteristics")
    public List<MaterialAttribute> characteristics = new ArrayList<MaterialAttribute>();

}
