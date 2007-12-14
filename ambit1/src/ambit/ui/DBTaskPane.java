package ambit.ui;

import java.awt.BorderLayout;

import javax.swing.ActionMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.l2fprod.common.swing.JTaskPane;
import com.l2fprod.common.swing.JTaskPaneGroup;

/**
 * Uses {@link com.l2fprod.common.swing.JTaskPaneGroup}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DBTaskPane extends JPanel {
	protected JTaskPane taskPane = null;
	public DBTaskPane(Object[][] actions) {
		super();
		addWidgets(actions);
	}
	public void addWidgets(Object[][] actions) {
		  taskPane = new JTaskPane();	
	      for (int i=0; i < actions.length;i++)  {
		      JTaskPaneGroup aGroup = new JTaskPaneGroup();
		      //aGroup.setIcon((ImageIcon)actions[i][2]);
		      aGroup.setExpanded(i<4);
		      aGroup.setTitle(actions[i][1].toString());
		      //aGroup.setToolTipText(RESOURCE
		       // .getString("Main.tasks.systemGroup.tooltip"));//
		      aGroup.setSpecial(true);
		      //systemGroup.setIcon(new ImageIcon(TaskPaneMain.class
		        //.getResource("icons/tasks-email.png")));
	
		      //systemGroup.add(makeAction(RESOURCE.getString("Main.tasks.email"), "",
		        //"icons/tasks-email.png"));
		      //systemGroup.add(makeAction(RESOURCE.getString("Main.tasks.delete"), "",
		        //"icons/tasks-recycle.png"));
		      Object[] keys = ((ActionMap)actions[i][0]).allKeys();
		      for (int j=0;j<keys.length;j++) {
		    	  aGroup.add(((ActionMap)actions[i][0]).get(keys[j]));
		      }
		      
		      taskPane.add(aGroup);
	      }
	      JScrollPane scroll = new JScrollPane(taskPane);
	      scroll.setBorder(null);

	      setLayout(new BorderLayout());
	      add(scroll,BorderLayout.CENTER);
	      
	      
	      setBorder(null);
	}
}
