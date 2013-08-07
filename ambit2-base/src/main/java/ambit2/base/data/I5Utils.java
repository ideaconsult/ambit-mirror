package ambit2.base.data;

public class I5Utils {
	private I5Utils() {
	}
	public static String[] splitI5UUID(String i5uuid) {
		String[] uuid = new String[] {null,i5uuid};
		if (uuid!=null) {
		int pos = uuid[1].indexOf("-");
			uuid[0] = uuid[1].substring(0,pos);
			uuid[1] = uuid[1].substring(pos+1,uuid[1].length());
		}
		return uuid;
	}
	/**
	 * http://docs.oracle.com/javase/6/docs/api/java/util/UUID.html#toString()
	 * @param u
	 * @return
	 */
	public static String addDashes(String u) {
		if (u.contains("-")) return u;
		return String.format("%s-%s-%s-%s-%s", 
					u.substring(0,8),
					u.substring(8,12),
					u.substring(12,16),
					u.substring(16,20),
					u.substring(20,32)
					);
	}
}
