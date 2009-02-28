package nplugins.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

import nplugins.core.Introspection;
import nplugins.core.PluginPackageEntry;

/**
 * Saves and restores plugins to tmp files - UNDER development!
 * @author nina
 *
 */
public class FileStorage extends Hashtable<String, String> implements IPluginsStorage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2204130021275581712L;
	public String getKey(PluginPackageEntry pkgEntry) {
		return pkgEntry.getClassName();
	}
	public String getKey(INanoPlugin plugin) {
		return plugin.getClass().getName();
	}
	

	public INanoPlugin restorePlugin(PluginPackageEntry pkgEntry) {
	    	String key = getKey(pkgEntry);
	    	String filename = get(key);
	    	if (filename == null) return null;
	    	File file = new File(filename);
	    	if (!file.exists()) return null;
	    	FileInputStream in = null;
	    	try {
	    		in = new FileInputStream(file);
	    		Object plugin = Introspection.loadObject(in, key);
	    		if (plugin instanceof INanoPlugin)
	    			return (INanoPlugin) plugin;
	    	} catch (Exception x) {
	    		x.printStackTrace();
	    	} finally {
	    		try { if (in != null) in.close(); } catch (Exception x) {}
	    	}
	    	return null;
	    	
	    }    
	public void savePlugin(INanoPlugin plugin) {
	    	String key = getKey(plugin);
			String homeDir = System.getProperty("user.home") +"/.ambit2/nplugins/";
			File dir = new File(homeDir);
			if (!dir.exists())	dir.mkdirs();		
			File file = new File(homeDir,key+".npg");
			ObjectOutputStream out=null;
			try {
				out = new ObjectOutputStream(new FileOutputStream(file));
				out.writeObject(plugin);
				out.flush();
				put(key, file.getAbsolutePath());
			} catch (Exception x) {
				x.printStackTrace();
				remove(key);
			} finally {
				try { if (out != null) out.close(); } catch (Exception x) {}
			}
			 
	    }
	public void removePlugin(INanoPlugin plugin) {
		//todo
		
	}
	    
}
