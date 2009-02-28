package nplugins.shell;

import java.util.Hashtable;

import nplugins.core.PluginPackageEntry;

/**
 * Saves and restores plugins - simple hashtable implementation
 * @author nina
 *
 */
public class MemStorage extends Hashtable<String, INanoPlugin> implements IPluginsStorage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9121913455843804968L;

	public String getKey(PluginPackageEntry pkgEntry) {
		return pkgEntry.getClassName();
	}
	public String getKey(INanoPlugin plugin) {
		return plugin.getClass().getName();
	}

	public INanoPlugin restorePlugin(PluginPackageEntry pkgEntry) {
		return get(getKey(pkgEntry));
	}

	public void savePlugin(INanoPlugin plugin) {
		put(getKey(plugin), plugin);
		
	}
	public void removePlugin(INanoPlugin plugin) {
		remove(getKey(plugin));
		
	}
}
