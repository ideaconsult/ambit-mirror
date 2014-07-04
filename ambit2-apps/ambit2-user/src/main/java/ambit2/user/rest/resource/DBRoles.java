package ambit2.user.rest.resource;

import java.util.List;

import net.idea.restnet.db.aalocal.DBRole;

import org.restlet.security.Role;

/**
 * Singleton helper class
 * @author nina
 *
 */
public class DBRoles {
	public static final DBRole adminRole = new DBRole(AMBITDBRoles.ambit_admin.name(),AMBITDBRoles.ambit_admin.toString());
	public static final DBRole datasetManager = new DBRole(AMBITDBRoles.ambit_datasetmgr.name(),AMBITDBRoles.ambit_datasetmgr.toString());
	public static final DBRole userRole = new DBRole(AMBITDBRoles.ambit_user.name(),AMBITDBRoles.ambit_user.toString());

	private DBRoles() {
		
	}
	public static boolean isAdminOrDatasetManager(List<Role> roles) {
		return (roles==null)?false:(
			 		(roles.indexOf(DBRoles.adminRole)>=0)||(roles.indexOf(DBRoles.datasetManager) >=0)
			 		);
	}	   
	public static boolean isAdmin(List<Role> roles) {
		return (roles==null)?false:(roles.indexOf(DBRoles.adminRole)>=0);
	}
	public static boolean isDatasetManager(List<Role> roles) {
		return (roles==null)?false:(roles.indexOf(DBRoles.datasetManager)>=0);
	}
	public static boolean isUser(List<Role> roles) {
		return (roles==null)?false:(roles.indexOf(DBRoles.userRole)>=0);
	}
}
