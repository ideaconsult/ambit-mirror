package nplugins.shell;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import nplugins.core.PluginPackageEntry;
import nplugins.core.PluginsPackageEntries;

/**
 * A table model for {@link PluginsPackageEntries}. Displayed on the left side of the plugin manager.
 * @author nina
 *
 */
public class PluginsTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5301337786926482145L;
	protected PluginsPackageEntries packages;
	public PluginsTableModel() {
		this(null);
	}
	public PluginsTableModel(PluginsPackageEntries packages) {
		setPackages(packages);
	}
	public PluginsPackageEntries getPackages() {
		return packages;
	}

	public void setPackages(PluginsPackageEntries packages) {
		this.packages = packages;
		fireTableStructureChanged();
	}

	public int getColumnCount() {
		
		return 2;
	}

	public int getRowCount() {
		if (packages != null)
			return packages.size();
		else return 0;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		PluginPackageEntry entry = packages.get(rowIndex);
		switch (columnIndex) {
	
		case 1: return entry.getTitle();
		case 0: return entry.getIcon();
		default: return "";
		}
	}
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0: return ImageIcon.class;
		default : return String.class;

		}
	}
	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0: return "Logo";
		case 1: return "Name";
		default: return "";
		}
	}

}
