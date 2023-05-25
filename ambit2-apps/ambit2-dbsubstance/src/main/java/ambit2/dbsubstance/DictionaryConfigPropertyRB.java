package ambit2.dbsubstance;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.logging.Level;

public class DictionaryConfigPropertyRB extends DictionaryConfig<PropertyResourceBundle> {
	


	public DictionaryConfigPropertyRB(String root) {
		this.root = root;
	}

	public String getString(_DICT dict,String key) {
		return getBundle(dict).getString(key);
	}
	@Override
	protected PropertyResourceBundle createBundle(_DICT dict) {
		File file  = new File(new File(getRoot()),String.format("%s.properties",dict.getResource()));
		try (InputStream pstream = new FileInputStream(file)) {
			return new PropertyResourceBundle(pstream);
		} catch (Exception err) {
			logger_cli.log(Level.WARNING, String.format("Not found\t%s\t%s", file.getAbsoluteFile()));
			return null;
		}
		  
	}
}
