package ambit2.dbui;

import ambit2.db.LoginInfo;
import ambit2.ui.editors.BeanEditor;


public class LoginPanel extends BeanEditor<LoginInfo> {

	public LoginPanel() {
		super(null,new String[] {
				"user",
				"password",
				null,
				"hostname",
				"port",
				"database",
				}
		,
		"Connect into database"
		);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1193357175996278794L;

}
