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
	public static final DBRole curatorRole = new DBRole(AMBITDBRoles.ambit_curator.name(),AMBITDBRoles.ambit_curator.toString());
	public static final DBRole userRole = new DBRole(AMBITDBRoles.ambit_user.name(),AMBITDBRoles.ambit_user.toString());

	private DBRoles() {
		
	}
	public static boolean isAdminOrCurator(List<Role> roles) {
		return (roles==null)?false:(
			 		(roles.indexOf(DBRoles.adminRole)>=0)||(roles.indexOf(DBRoles.curatorRole) >=0)
			 		);
	}	   
	public static boolean isAdmin(List<Role> roles) {
		return (roles==null)?false:(roles.indexOf(DBRoles.adminRole)>=0);
	}
	public static boolean isCurator(List<Role> roles) {
		return (roles==null)?false:(roles.indexOf(DBRoles.curatorRole)>=0);
	}
	public static boolean isUser(List<Role> roles) {
		return (roles==null)?false:(roles.indexOf(DBRoles.userRole)>=0);
	}
}
