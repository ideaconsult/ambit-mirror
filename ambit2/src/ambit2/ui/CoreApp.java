/**
 * Created on 2005-1-18
 *
 */
package ambit2.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;


/**
 * A framework for a Swing application
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
abstract public class CoreApp implements ActionListener {
    //Specify the look and feel to use.  Valid values:
    //null (use the default), "Metal", "System", "Motif", "GTK+"
    final static String LOOKANDFEEL = "System";
    protected String caption = "Application";
    
    protected JFrame mainFrame = null;
    protected JComponent toolBar = null;
    protected JPanel mainPanel = null;
    protected JPanel statusBar = null;
    protected int h=360; 
    protected int w = 360;

   
	/**
	 * 
	 */
	public CoreApp(String title, int width, int height, String[] args) {
		w = width; h = height;
          
		
		caption = title;
        mainFrame = new JFrame(title);
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.setSize(new Dimension(w,h));
        mainFrame.addWindowListener( new WindowAdapter() {
        	/*
        	   public void windowOpened( WindowEvent e ){
        	   		mainFrame.requestFocus();
        	     }
        	    */ 
            	
        	   public void windowClosing( WindowEvent e ){
        	     if (canClose()) doClose();  
        	   }
        	   } ); 
    
		ImageIcon icon = getIcon();
		if (icon != null)
			mainFrame.setIconImage(icon.getImage());
        
        initSharedData(args) ;      
        
        //Create and set up the panel.
        mainPanel = new JPanel();
        toolBar = createToolbar();
        JMenuBar menuBar = createMenuBar();
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(menuBar,BorderLayout.NORTH);
        if (toolBar != null)
        	topPanel.add(toolBar,BorderLayout.CENTER);
        
        createWidgets(mainFrame,mainPanel);
        //Add the panel to the window.
        
        Container ct = mainFrame.getContentPane();

        ct.add(topPanel,BorderLayout.NORTH);
        ct.add(mainPanel,BorderLayout.CENTER);
        statusBar = createStatusBar();        
    	ct.add(statusBar,BorderLayout.SOUTH);

        //Display the window.
    	centerScreen();
        mainFrame.pack();
        mainFrame.setVisible(true);        
	}
	protected JComponent createToolbar() {
	    JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL  ) ;
        toolBar.setPreferredSize(new Dimension(w,32));
        toolBar.setFloatable(false);
        return toolBar;
	}
	protected JMenu createEditMenu() {
		return UITools.createEditMenu(mainFrame);
	}
    protected JMenu createStyleMenu() {
        return UITools.createStyleMenu();
    }

	abstract protected void initSharedData(String args[]) ;
	
	protected JPanel createStatusBar() {
		JPanel sb = new JPanel();
		sb.setPreferredSize(new Dimension(w,18));
		sb.setLayout(new BorderLayout());
		sb.add(new JLabel(""), BorderLayout.CENTER);

		sb.setBorder(new BevelBorder(BevelBorder.LOWERED));
		return sb;	

	}
	
	
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    protected static void createAndShowGUI() {
        //Set the look and feel.
        initLookAndFeel();
        
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);
        
        
        //to create application object here
    }
    private static void initLookAndFeel() {
        String lookAndFeel = null;

        if (LOOKANDFEEL != null) {
            if (LOOKANDFEEL.equals("Metal")) {
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            } else if (LOOKANDFEEL.equals("System")) {
                lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            } else if (LOOKANDFEEL.equals("Motif")) {
                lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
            } else if (LOOKANDFEEL.equals("GTK+")) { //new in 1.4.2
                lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
            } else {
                System.err.println("Unexpected value of LOOKANDFEEL specified: "
                                   + LOOKANDFEEL);
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            }

            try {
                UIManager.setLookAndFeel(lookAndFeel);
            } catch (ClassNotFoundException e) {
                System.err.println("Couldn't find class for specified look and feel:"
                                   + lookAndFeel);
                System.err.println("Did you include the L&F library in the class path?");
                System.err.println("Using the default look and feel.");
            } catch (UnsupportedLookAndFeelException e) {
                System.err.println("Can't use the specified look and feel ("
                                   + lookAndFeel
                                   + ") on this platform.");
                System.err.println("Using the default look and feel.");
            } catch (Exception e) {
                System.err.println("Couldn't get specified look and feel ("
                                   + lookAndFeel
                                   + "), for some reason.");
                System.err.println("Using the default look and feel.");
                e.printStackTrace();
            }
        }
    }
    

	abstract protected JMenuBar createMenuBar();
    /* implementation will look like 
     * MyApp app = new MyApp();
     */	
    
    abstract public void actionPerformed(ActionEvent event);    
    abstract protected void createWidgets(JFrame aFrame, JPanel aPanel);	
	
	public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
   }
	/**
	 * @return Returns the caption.
	 */
	protected String getCaption() {
		return caption;	
	}
	/**
	 * @param caption The caption to set.
	 */
	protected void setCaption(String caption) {
		this.caption = caption;
		mainFrame.setTitle(caption);
	}
	protected void showToBeDoneMessage(String message) {
		showMessage("to be done",message);
	}
	public  static void showMessage(String caption,String message) {
		JOptionPane.showMessageDialog( null,
			caption,
		    message,						
		    JOptionPane.INFORMATION_MESSAGE);
	}
	
	protected void showJavaInfo() {
		String[] props = {
				"java.version",
				"java.class.path",
				"java.class.version","java.home",
				"java.vendor","java.vendor.url",
				"os.arch","os.name","os.version",
				"user.dir","user.home","user.name",
				"file.separator","path.separator",
				"line.separator",
				"javawebstart.version",
				"jnlpx.heapsize","jnlpx.home",
				"jnlpx.jvm","jnlpx.remove",
				"jnlpx.splashport"
				};
		StringBuffer b = new StringBuffer();
		String s;
		for (int i = 0; i < props.length; i++) {
			b.append(props[i]);
			b.append(" = ");
			s = System.getProperty(props[i]);
			if (s == null) {
				b.append("NA\n");				
				continue;
			}
				
			if (s.length() < 80) { 
				b.append(s);	b.append('\n');			
			}
			else
				for (int p = 0; p< s.length(); p+=80) {
					if ((p+80) >= s.length()) 
						b.append(s.substring(p));
					else b.append(s.substring(p,p+80));
					b.append('\n');					
				}	

		}	
		showMessage(b.toString(),"Java info");
	}
	public void centerScreen() {
		  Dimension dim = mainFrame.getToolkit().getScreenSize();
		  Rectangle abounds = mainFrame.getBounds();
		  mainFrame.setLocation((dim.width - abounds.width) / 2,
		      (dim.height - abounds.height) / 2);
	}
		 
	
    protected boolean canClose() {
        return (JOptionPane.showConfirmDialog(null,"Are you sure to exit ?","Please confirm",JOptionPane.YES_NO_OPTION)
        		==JOptionPane.YES_OPTION);
     }
    protected void doClose() {
          mainFrame.setVisible(false);
          mainFrame.dispose();
 		  Runtime.getRuntime().runFinalization();						 
		  Runtime.getRuntime().exit(0);          
     }
    protected ImageIcon getIcon() { return null; }
	
}

