package ambit2.rest.task.dsl;

import org.restlet.data.Reference;

public class OTCompound extends OTObject {

	 protected OTCompound(Reference ref) {
			super(ref);
		 }
		 protected OTCompound(String ref) {
			super(ref);
		}
		 public static OTCompound compound(Reference uri) throws Exception  { 
			    return new OTCompound(uri);
		 }

		 public static OTCompound compound(String uri) throws Exception  { 
			    return new OTCompound(uri);
		 }
}
