package nplugins.shell;

import nplugins.core.PluginPackageEntry;

public interface IPluginsStorage {
	String getKey(PluginPackageEntry pkgEntry);
	String getKey(INanoPlugin plugin);	
	void savePlugin(INanoPlugin plugin) ;
	INanoPlugin restorePlugin(PluginPackageEntry pkgEntry) ;
}
