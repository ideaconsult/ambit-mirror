package ambit2.ui.query;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ambit2.data.AmbitUser;
import ambit2.database.data.ISharedDbData;
import ambit2.ui.CorePanel;

public class DbStatusPanel extends CorePanel implements Observer {
	protected JTextArea textArea ;
	public DbStatusPanel(String title) {
		super(title);
	}

	public DbStatusPanel(String title, Color bClr, Color fClr) {
		super(title, bClr, fClr);
	}

	protected void addWidgets() {
		setLayout(new BorderLayout());
		textArea = new JTextArea();
		textArea.setFont(new Font("Courier", Font.PLAIN,12));
		textArea.setEditable(false);
		JScrollPane p = new JScrollPane(textArea);
		setBorder(BorderFactory.createTitledBorder("Database info"));
		p.setBorder(BorderFactory.createLineBorder(Color.black));
		p.setPreferredSize(new Dimension(200,200));
		p.setMinimumSize(new Dimension(50,50));
		add(p,BorderLayout.CENTER);
	}
	/* (non-Javadoc)
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
		if (o instanceof ISharedDbData) {
		    ISharedDbData dba = (ISharedDbData) o;
			textArea.setText("");
			textArea.append("Database\t");
			textArea.append(dba.getDatabase());
			textArea.append("\n");
			textArea.append("Host\t");
			textArea.append(dba.getHost());
			textArea.append("\n");
			textArea.append("Port\t");
			textArea.append(dba.getPort());
			textArea.append("\n");			
			textArea.append("User\t");
			AmbitUser user = dba.getUser();
			if (user != null) {
				textArea.append(user.getName());
				textArea.append("\nUser type\t");
				textArea.append(user.getUserType());
				textArea.append("\n");
				if (dba.getDbConnection().isClosed()) textArea.append("Not connected");
				else textArea.append("Connected");
			} else textArea.append("Not connected");
			textArea.append("\n");
		}
    }
    public void setText(String text) {
    	textArea.setText(text);
    }
}
