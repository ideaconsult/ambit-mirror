/*
 * Copyright Ideaconsult Ltd. (C) 2005-2008 
 *
 * Contact: nina@acad.bg
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

*/
package nplugins.shell.application;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

import nplugins.shell.INPApplicationContext;
import nplugins.shell.INanoPlugin;
import nplugins.shell.NanoPluginsManager;

import com.jgoodies.looks.Options;


/**
 * Nanoplugins skeleton application main class. Search for and loads plugins and creates tabs for each plugin.
<h3>Plugins</h3>

 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2008-12-4
 */
public class NPluginsApplication implements PropertyChangeListener {
	final static String LOOKANDFEEL = "System"; //"System";
	final static String THEME = "Ocean";
	protected static String[] cmdOpts = null;
    private final INPApplicationContext applicationContext;
    private NanoPluginsManager pluginsManager;
    protected static Logger logger = Logger.getLogger("nplugins.application.NPluginsApplication");
    
    protected final SimpleInternalFrame rightPanel;
    protected final SimpleInternalFrame leftPanel;
    protected final SimpleInternalFrame detailsPanel;

	JFrame mainFrame;
	
	public NPluginsApplication(String title, int width, int height, String[] args) {
		super();
        applicationContext = createApplicationContext();
        
		initialize(args);
		
      
        mainFrame = new JFrame(title);
        //TODO make it configurable
		ImageIcon icon = createImageIcon();
		if (icon != null) mainFrame.setIconImage(icon.getImage());
		mainFrame.setTitle(getTitle());
        Dimension dim = mainFrame.getToolkit().getScreenSize();
        if (height > dim.getHeight()) height = (int)(4*dim.getHeight()/5);
        if (width > dim.getWidth()) height = (int)(4*dim.getWidth()/5);

        mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainFrame.setSize(new Dimension(width, height));
        mainFrame.addWindowListener( new WindowAdapter() {
        	   @Override
			public void windowClosing( WindowEvent e ){
        	     if (canClose()) doClose();  
        	   }
      	   } ); 

        leftPanel = new SimpleInternalFrame("Options");
        leftPanel.setPreferredSize(new Dimension(150, 100));
        leftPanel.setContent(buildMainLeftPanel(pluginsManager));
        
        rightPanel = new SimpleInternalFrame("Main");
        rightPanel.setPreferredSize(new Dimension(400,400));
        rightPanel.setContent(buildMainRightPanel(pluginsManager));
        
        detailsPanel = new SimpleInternalFrame("Details");
        detailsPanel.setPreferredSize(new Dimension(400,100));
        JComponent details = buildDetailsPanel(pluginsManager);
        if (details != null)
        	detailsPanel.setContent(details);
        detailsPanel.setVisible(details != null);
        
		mainFrame.getContentPane().setLayout(new BorderLayout());
		mainFrame.getRootPane().setJMenuBar(createMenuBar());
		mainFrame.getContentPane().add( createToolBar(),BorderLayout.NORTH);
		mainFrame.getContentPane().add( createMainPanel(mainFrame),BorderLayout.CENTER);
		mainFrame.getContentPane().add( createStatusBar(),BorderLayout.SOUTH);
		mainFrame.setLocation(0,0);
        mainFrame.pack();
        mainFrame.setVisible(true);
        int state = mainFrame.getExtendedState();
        state |= Frame.MAXIMIZED_BOTH;
        mainFrame.setExtendedState(state);        
		 
	}
	protected INPApplicationContext createApplicationContext() {
        try {
            return new FileSystemXMLApplicationContext(getClass());
        } catch (Exception x) {
            return new DefaultAppplicationContext();
        }
    }
    protected void setPlugin(INanoPlugin plugin) {
        
    }
	protected JComponent createToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		Border border = new EmptyBorder(2, 6, 2, 6); // top, left, bottom, right
		ButtonGroup group = new ButtonGroup();
        ActionMap map = pluginsManager.getActions();
        Object[] keys = map.allKeys();
		for (int i=0; i < keys.length; i++) 
			try {
				Action action = map.get(keys[i]);
                if (action instanceof NPluginsAction)
                    ((NPluginsAction)action).setTaskMonitor(applicationContext.getTaskMonitor());
			    toolBar.add(createButton(action, group,border));

			} catch (Exception x) {
				logger.severe(x.getMessage());
			}
		return toolBar;
	}
    
    protected JToggleButton createButton(Action action,ButtonGroup group,Border border) {
        JToggleButton button = new JToggleButton(action);
        button.setSelected(false);
        group.add(button);
        button.setBorder(border);
        button.setVerticalTextPosition(JButton.BOTTOM);
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setFocusable(false);
   
        return button;
    }
	protected JComponent createStatusBar() {
		return new StatusBar(applicationContext.getTaskMonitor());
	}
	protected JMenuBar createMenuBar() {
		JMenuBar bar =  new JMenuBar();
		bar.add(createFileMenu());
		bar.add(createAboutMenu());
		return bar;
	}
	protected void initialize(String[] args) {
		pluginsManager = new NanoPluginsManager();
		pluginsManager.setParameters(args);
		pluginsManager.setApplicationContext(applicationContext);
        
		addPlugins(pluginsManager);

        pluginsManager.addPropertyChangeListener(this);        
	}
	
	protected void addPlugins(NanoPluginsManager manager) {
		try {
			manager.addPackage("nplugins.demo.DemoPlugin");
			manager.addPackage("nplugins.demo.DemoPlugin",new String[] {"1"},"Demo",null);
			manager.addPackage("nplugins.demo.DemoPlugin",new String[] {"2"},"Demo",null);
			manager.addPackage("nplugins.workflow.MWorkflowPlugin",new String[] {"2"},"MWorkflow",null);
			
		} catch (Exception x) {
            x.printStackTrace();
			logger.severe(x.getMessage());
		}		
	}
	protected JComponent createMainPanel(Component parent) {
        JSplitPane mainpane = UIFSplitPane.createStrippedSplitPane(

                JSplitPane.VERTICAL_SPLIT,
                rightPanel,
                detailsPanel
                );
        mainpane.setResizeWeight(0.8f);        	        
        mainpane.setOpaque(false);
        
        JSplitPane pane = UIFSplitPane.createStrippedSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                leftPanel,
                mainpane
                );
        pane.setResizeWeight(0.2f);        	        
        pane.setOpaque(false);
        
        return pane;
	}
	
    private JComponent buildMainLeftPanel(INanoPlugin plugin) {
        JComponent c = null;
        JComponent[] options = plugin.createOptionsComponent();
        if (options!= null) 
            if (options.length > 1) {
                JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.BOTTOM);
                tabbedPane.putClientProperty(Options.EMBEDDED_TABS_KEY, Boolean.TRUE);
                for (int i=0; i < options.length; i++)
                	tabbedPane.addTab(Integer.toString(i+1), options[i]);
                c = tabbedPane;
            } else c = options[0];
        else 
            c = new JLabel("No options");
        return c;
    }
    
    private JComponent buildDetailsPanel(INanoPlugin plugin) {
        JComponent c = null;
        JComponent[] options = plugin.createDetailsComponent();
        if (options!= null) 
            if (options.length > 1) {
                JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.BOTTOM);
                tabbedPane.putClientProperty(Options.EMBEDDED_TABS_KEY, Boolean.TRUE);
                for (int i=0; i < options.length; i++)
                	tabbedPane.addTab(Integer.toString(i+1), options[i]);
                c = tabbedPane;
            } else c = options[0];
        return c;
    }
    
    
    private Component buildMainRightPanel(INanoPlugin plugin) {
    	 
    	 Component c = plugin.createMainComponent().getComponent();
    	 if (c == null) c = new JLabel("N/A");
    	 return c;
    }
        
	
    public JMenu createFileMenu() {
        JMenu menu = new JMenu("File");
        menu.add(new AbstractAction("Exit") {
        	/**
             * 
             */
            private static final long serialVersionUID = 6596627117263052884L;

            public void actionPerformed(ActionEvent arg0) {
        		if (canClose()) {
	       		 Runtime.getRuntime().runFinalization();						 
	    		 Runtime.getRuntime().exit(0);
        		}
        	}
        });
        return menu;
    }    
    public JMenu createAboutMenu() {
        JMenu menu = new JMenu("Help");
        menu.add(new AbstractAction("About") {
        	/**
			 * 
			 */
			private static final long serialVersionUID = -9037830763076468481L;

			public void actionPerformed(ActionEvent arg0) {
        		Package jcpPackage = Package.getPackage("nplugins.core");
        		//version will be only available if started from jar file
        		//version is specified in package manifest 
        		// See MANIFESTAPP.MFT file
        		String version = jcpPackage.getImplementationVersion();
        		
        		ImageIcon icon  = null;
        		try {
        			icon = createImageIcon();
        		} catch (Exception x) {
        			icon =null;
        		}
        		String m = "<html>shell v" + version +  
				"<br>Developed by <font color='#0000FF'><u>http://ambit.acad.bg</u></font><br>e-mail <u>nina@acad.bg</u><br></html>";
        		JLabel logo = new JLabel(m,icon,SwingConstants.CENTER);
        		logo.setToolTipText("Click here to go to ambit site");
        		logo.addMouseListener(new MouseAdapter(){
        			@Override
					public void mouseClicked(MouseEvent arg0) {
        				super.mouseClicked(arg0);
        				//openURL("http://ambit.acad.bg");
        			}
        		});        		
        		JOptionPane.showMessageDialog(mainFrame,logo,"About",JOptionPane.PLAIN_MESSAGE);
        		
        	}
        }) ;
 
    	return menu;
        
    }

    protected boolean canClose() {
    	
    	String msgexit = "Are you sure you want to exit?";
        return (JOptionPane.showConfirmDialog(null,msgexit,"Please confirm",JOptionPane.YES_NO_OPTION)
        		==JOptionPane.YES_OPTION);
     }
    protected void doClose() {
          mainFrame.setVisible(false);
          mainFrame.dispose();
 		  Runtime.getRuntime().runFinalization();						 
		  Runtime.getRuntime().exit(0);          
     }
	
    private static void initLookAndFeel(String LOOKANDFEEL) {
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
                
            } else if (LOOKANDFEEL.equals("Mac")) {
            	//lookAndFeel = "javax.swing.plaf.mac.MacLookAndFeel";
            	lookAndFeel = "com.sun.java.swing.plaf.mac.MacLookAndFeel";
            } else {
                logger.severe("Unexpected value of LOOKANDFEEL specified: "
                                   + LOOKANDFEEL);
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            }

            try {
                UIManager.setLookAndFeel(lookAndFeel);
                if (LOOKANDFEEL.equals("Metal")) {
                    if (THEME.equals("DefaultMetal"))
                       MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                    else if (THEME.equals("Ocean"))
                       MetalLookAndFeel.setCurrentTheme(new OceanTheme());
                    else
                       MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                       
                    UIManager.setLookAndFeel(new MetalLookAndFeel()); 
                  }	
            } catch (ClassNotFoundException e) {
                logger.warning("Couldn't find class for specified look and feel:"
                                   + lookAndFeel);
                logger.warning("Did you include the L&F library in the class path?");
                logger.info("Using the default look and feel.");
            } catch (UnsupportedLookAndFeelException e) {
                logger.warning("Can't use the specified look and feel ("
                                   + lookAndFeel
                                   + ") on this platform.");
                logger.info("Using the default look and feel.");
            } catch (Exception e) {
                logger.warning("Couldn't get specified look and feel ("
                                   + lookAndFeel
                                   + "), for some reason.");
                logger.info("Using the default look and feel.");
                logger.warning(e.getMessage());
            }
        }
    }

    protected static void createAndShowGUI() {
        initLookAndFeel(LOOKANDFEEL);
        JFrame.setDefaultLookAndFeelDecorated(true);
//      to create application object here
    }
    

	/**
	 * 
	 * @param args
<pre>

</pre> 
	 */
	public static void main(final String[] args) {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
                new NPluginsApplication("Nano plugins",800,800,args);
            }
        });
   }
	
    public ImageIcon createImageIcon() {
    	return Utils.createImageIcon("nplugins/shell/resources/plugin.png");
    }	

    public String getTitle() {
		Package jcpPackage = Package.getPackage("nplugins.application");
		//version will be only available if started from jar file
		//version is specified in package manifest 
		// See MANIFESTAPP.MFT file
		if (jcpPackage != null) {
			String version = jcpPackage.getImplementationVersion();
			String title = jcpPackage.getImplementationTitle();
			if ((version != null) && (title != null)) return title+version;
    	}
		return "Nano Plugins shell";
    	
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (NanoPluginsManager.property_plugin.equals(evt.getPropertyName())) 
            if (evt.getNewValue() instanceof INanoPlugin) {
                INanoPlugin p = (INanoPlugin)evt.getNewValue();
                JComponent c = buildMainLeftPanel(p);
                leftPanel.setContent(c);
                leftPanel.setTitle(c.toString());
                rightPanel.setContent(buildMainRightPanel(p));
                JComponent detail = buildDetailsPanel(p);
                detailsPanel.setContent(detail);
                detailsPanel.setVisible(detail!=null);
                String title = p.toString();
                if (title == null) title = " ";
                rightPanel.setTitle(title);
                rightPanel.setFrameIcon(p.getIcon());
                
                
            }
        
        
    }
}
