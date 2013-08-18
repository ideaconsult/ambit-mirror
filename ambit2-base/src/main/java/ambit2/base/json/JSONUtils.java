package ambit2.base.json;

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
	/**
	 * Returns null, if the value is null, or a quoted string otherwise
	 * @param value
	 * @return
	 */
	public static String jsonQuote(String value) {
		if(value==null) return null;
		else return String.format("\"%s\"",value);
	}
	/**
	 * Used for JSONP callbacks
	 * TODO: full name validation
	 * @param functionName
	 * @return
	 */
	public static String jsonSanitizeCallback(String functionName) {
		if(functionName==null) return null;
		else return functionName.replace("(","").replace(")","");
	}
}
