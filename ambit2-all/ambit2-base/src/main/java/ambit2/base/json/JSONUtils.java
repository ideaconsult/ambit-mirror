package ambit2.base.json;

/**
 * http://www.ietf.org/rfc/rfc4627.txt
 * @author nina
 *
 */
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
        .replace("\"", "\\\"")
		.replace("\u0016", "")
		.replace("\u0019", "");
		
		
		/*
		 * 
		 %x22 /          ; "    quotation mark  U+0022
         %x5C /          ; \    reverse solidus U+005C
         %x2F /          ; /    solidus         U+002F
         %x62 /          ; b    backspace       U+0008
         %x66 /          ; f    form feed       U+000C
         %x6E /          ; n    line feed       U+000A
         %x72 /          ; r    carriage return U+000D
         %x74 /          ; t    tab             U+0009
         %x75 4HEXDIG )  ; uXXXX                U+XXXX
         */
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
