package ambit2.rest.json;

public class JSONUtils {
	public static String jsonEscape(String value) {
		if(value==null) return null;
		else return value.replace("\\", "\\\\")
        //.replace("/", "\\/")
        .replace("\b", "\\b")
        .replace("\f", "\\f")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
        .replace("\"", "\\\"");
	}
}
