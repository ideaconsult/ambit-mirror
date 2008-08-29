package ambit2.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.WindowConstants;



import com.jgoodies.forms.debug.FormDebugPanel;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class WizardPanel extends FormDebugPanel {
	public enum ANSWER_MODE {
	    back,next,finish,help,cancel,none 
	}
	protected ANSWER_MODE answer=ANSWER_MODE.none;
	protected final String dlu = "12dlu";
	/**
	 * 
	 */
	private static final long serialVersionUID = -4624498162154536082L;
	public WizardPanel(String subtitle,Component component,String help) {
		super();
		buildPanel(buildLeft(help),buildCenter(subtitle,component),buildNavigation(true));
	}
	protected Component buildLeft(String help) {
		if (help == null) return buildGraphicsPanel();
		return buildHelpPanel(help);
	}
	protected Component buildCenter(String subtitle,Component component) {
		return  buildMainPanel(subtitle, component);
	}
	protected Component buildNavigation(boolean grouped) {
        FormLayout layout = new FormLayout(
            "pref, 6dlu, pref, 6dlu,pref, 6dlu:grow, 12dlu, pref, 6dlu, pref",
            "pref");
        if (grouped) {
            layout.setColumnGroups(new int[][]{{1, 5, 6, 8, 10}});
        }
        JPanel panel = buildPanel(layout);
        CellConstraints cc = new CellConstraints();

        JButton backButton = new JButton("< Back");
        JButton nextButton = new JButton("Next >");
        JButton finishButton = new JButton("Finish");
        JButton helpButton = new JButton("Help");
        JButton cancelButton = new JButton("Cancel");
        
        
        backButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		back(e);
        	}
        });
        nextButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		next(e);
        	}
        });
        finishButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		finish(e);
        	}
        });
        cancelButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		cancel(e);
        	}
        });        
        panel.add(backButton, cc.xy(1, 1));
        panel.add(nextButton, cc.xy(3, 1));
        panel.add(finishButton, cc.xy(5, 1));
        panel.add(helpButton,   cc.xy(8, 1));
        panel.add(cancelButton, cc.xy(10, 1,CellConstraints.RIGHT,CellConstraints.BOTTOM));

        return panel;
    }
	public void back(ActionEvent e) {
		setAnswer(ANSWER_MODE.back);
		JOptionPane.getFrameForComponent(this).setVisible(false);
	}
	public void next(ActionEvent e) {
		setAnswer(ANSWER_MODE.next);
		JOptionPane.getFrameForComponent(this).setVisible(false);

	}
	public void finish(ActionEvent e) {
		setAnswer(ANSWER_MODE.finish);
		JOptionPane.getFrameForComponent(this).setVisible(false);
	}	
	public void cancel(ActionEvent e) {
		setAnswer(ANSWER_MODE.cancel);
		JOptionPane.getFrameForComponent(this).setVisible(false);
	}
	protected Component buildGraphicsPanel() {
        FormLayout layout = new FormLayout(
        		"153dlu",
        		"top:276px"
                );
        CellConstraints cc = new CellConstraints();        
        JPanel p = buildPanel(layout);
       	//JLabel label = new JLabel(Utils.createImageIcon("ambit2/application/resources/splash.png"));
        JLabel label = new JLabel();
       	p.add(label,cc.xywh(1,1,1,1));
		return p;
	}	
	protected Component buildHelpPanel(String help) {
        FormLayout layout = new FormLayout(
        		//"right:[40dlu,pref]",
        		"153dlu",
                //"12dlu, 12dlu, fill:[pref,264dlu]"
        		"12dlu, top:276px"
                );
        CellConstraints cc = new CellConstraints();        
        JPanel p = buildPanel(layout);
        
       	p.add(createSeparator("&Help"),cc.xyw(1,1,1));
        JTextArea ta = new JTextArea(help);
        ta.setForeground(new Color(0,128,250));
        ta.setEditable(false);
        p.add(ta,cc.xywh(1,2,1,1));
		return p;
	}
	protected Component buildMainPanel(String subtitle,Component main) {
        FormLayout layout = new FormLayout(
        		//"right:[40dlu,pref]",
        		//"left:[pref,471px]:grow",
        		"fill:pref:grow",
                "pref, top:[pref,264px]:grow"
                );
        CellConstraints cc = new CellConstraints();        
        JPanel p = buildPanel(layout);
        p.add(createSeparator(subtitle),cc.xywh(1,1,1,1));
        p.add(main,cc.xywh(1,2,1,1));
		return p;
	}	
	
	protected void buildPanel(Component left, Component center, Component navigation) {
		FormLayout layout = new FormLayout(
	            "12dlu,left:[pref,153px]:grow,12dlu, left:[pref,471dlu]:grow,12dlu",  //columns
				"12dlu,top:[pref,328dlu]:grow, 12dlu,fill:48dlu:grow");  //rows
		setLayout(layout);
        CellConstraints cc = new CellConstraints();
	     
	    add(left,       cc.xywh (2,  2, 1, 3));	     
	    add(center,     cc.xywh (4,  2, 1, 1));
	    add(createSeparator(""),cc.xywh(2,3,3,1));
	    add(navigation,     cc.xywh (4,  4, 1, 1));
	}

	
	public ANSWER_MODE display(Frame owner, String title,  boolean modal) {
		JDialog frame = new JDialog(owner,title,modal);
		try {
	        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	        frame.getContentPane().add(this);
	        frame.pack();
	        frame.setLocationRelativeTo(owner);      
	        frame.setVisible(true);        
		} finally {
			frame.dispose();
		}
        return getAnswer();
    }
	protected JPanel buildPanel(FormLayout layout) {
		//return new FormDebugPanel(layout);
		return new JPanel(layout);
	}
    private Component createSeparator(String textWithMnemonic) {
        return DefaultComponentFactory.getInstance().createSeparator(
                textWithMnemonic);
    }
	public ANSWER_MODE getAnswer() {
		return answer;
	}
	public void setAnswer(ANSWER_MODE answer) {
		this.answer = answer;
	}	
}
