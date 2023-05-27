package ambit2.base.data.lookup;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DictionaryConfig<RB extends ResourceBundle> {
	protected static Logger logger_cli = Logger.getLogger(DictionaryConfig.class.getName());
	protected String root;
	protected Map<_DICT,RB> bundles = new HashMap<_DICT,RB>();

	public DictionaryConfig(String root) {
		this.root = root;
	}
	public DictionaryConfig() {
		this("net/idea/nanodata");
	}	
	public String getString(_DICT dict,String key) {
		return getBundle(dict).getString(key);
	}
	protected RB createBundle(_DICT dict) {
		return (RB)ResourceBundle.getBundle(getPath(dict),Locale.ENGLISH);
	}
	protected RB getBundle(_DICT dict) {
		if (bundles.get(dict)==null)
			bundles.put(dict, createBundle(dict));
		return bundles.get(dict);
	}
	protected String getRoot() {
		return root;
	}
	public String getPath(_DICT dict) {
		return String.format("%s/%s",getRoot(),dict.getResource());
	}
	public String getMappedString(String key,  _DICT dict) {
		return getMappedString(key, dict, key);
	}

	public String getMappedString(String key, _DICT dict, String keydefault) {
		try {
			RB bundle = getBundle(dict);
			if (bundle==null)
				return key;
			else
				return key == null ? keydefault : bundle.getString(key);
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, String.format("Not found\t%s\t%s", key, getBundle(dict).getBaseBundleName()));
			return keydefault;
		}
	}	
}
