package ambit2.ambitxt;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.prefs.BackingStoreException;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.BorderUIResource;

import nplugins.core.PluginClassPath;
import nplugins.shell.INanoPlugin;
import nplugins.shell.NanoPluginsManager;
import nplugins.shell.application.NPluginsApplication;
import nplugins.shell.application.Utils;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.border.DropShadowBorder;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.plaf.PainterUIResource;
import org.jdesktop.swingx.plaf.basic.BasicTaskPaneContainerUI;
import org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI;

import ambit2.mopac.MopacShell;
import ambit2.namestructure.Name2StructureProcessor;

public class AmbitXT extends NPluginsApplication {
	public AmbitXT(String title, int width, int height, String[] args) {
		super(title,width,height,args);
		//this is a quick dirty hack to make the builder include mopac and name structure packages ...
		Package pkg = getClass().getPackage();
		System.out.println(pkg);
		System.out.println(pkg.getImplementationTitle());
		System.out.println(pkg.getImplementationVendor());
		try {
			MopacShell mopac = new MopacShell();
			Name2StructureProcessor p = new Name2StructureProcessor();
			p.process("benzene");
		} catch (Throwable x) {
			x.printStackTrace();
		}
		leftPanel.setTitle("Workflow");
		rightPanel.setTitle(" ");
		detailsPanel.setTitle(" ");
	}
	@Override
    protected JComponent buildMainLeftPanel(INanoPlugin plugin) {
        JComponent c = null;
        JComponent[] options = plugin.createOptionsComponent();
        if (options!= null) 
            if (options.length > 1) {
            	JPanel p = new JPanel(new BorderLayout()) {
            		public String toString() {
            			return "Workflow";
            		}            		
            	};
            	p.add(options[0],BorderLayout.CENTER);
            	JXTaskPaneContainer taskPane = createContainer();
            	
                for (int i=1; i < options.length; i++) {
  	    		  	taskPane.add(addPane(options[i],taskPane));
                }
                p.add(taskPane,BorderLayout.SOUTH);
                p.setToolTipText("The active workflow");
                c = p;
            } else c = options[0];
        else 
            c = new JLabel("No options");
        
        return c;
    }	

	protected JXTaskPaneContainer createContainer() {
		Hashtable<String , Object> oldValues = saveSettings(new String[] {
				"TaskPaneContainer.border",JXTaskPaneContainer.uiClassID,"TaskPaneContainer.backgroundPainter","TaskPaneContainer.border"});
		UIManager.put("TaskPaneContainer.opaque", Boolean.FALSE);
		UIManager.put("TaskPaneContainer.border", new BorderUIResource(BorderFactory.createMatteBorder(0,0,0,0,getBackground())));		
  	    UIManager.put(JXTaskPaneContainer.uiClassID, "ambit2.ambitxt.AmbitTaskPaneContainerUI");	
  	    JXTaskPaneContainer container = new JXTaskPaneContainer();
		  UIManager.put("TaskPaneContainer.backgroundPainter", new PainterUIResource(
	              new MattePainter<JXTaskPaneContainer>(new GradientPaint(0f, 0f,
	                      Color.white, 0f, 1f, getBackground()), true)));	  	    
		container.setUI(new AmbitTaskPaneContainerUI());
		restoreSettings(oldValues);
		return container;
	}
	
	public Component addPane(Component comp,JXTaskPaneContainer container) {
		Hashtable<String , Object> oldValues = saveSettings(new String[] {
				"TaskPane.specialTitleBackground",
				"TaskPane.specialTitleForeground",
				"TaskPane.titleForeground",
				"TaskPane.titleOver",
				"TaskPane.specialTitleOver",
				"TaskPane.borderColor",
				"TaskPane.background",
				"TaskPane.animate",
				"TaskPane.border"						
		}
		);		
	
		UIManager.put("TaskPane.border", new BorderUIResource(BorderFactory.createMatteBorder(0,0,0,0,getBackground())));		
		UIManager.put("TaskPane.specialTitleBackground", getBackground());
		UIManager.put("TaskPane.specialTitleForeground", getForeground());		  
		UIManager.put("TaskPane.titleForeground", getForeground());
		UIManager.put("TaskPane.specialTitleOver", Color.blue);			
		UIManager.put("TaskPane.titleOver", Color.blue);				
		UIManager.put("TaskPane.borderColor", getBackground());		
		UIManager.put("TaskPane.background", getBackground());		
		UIManager.put("TaskPane.animate", Boolean.FALSE);				
		JXTaskPane pane = new JXTaskPane();
		pane.setUI(new AmbitPaneUI());
		pane.setName(comp.toString());
	    pane.setTitle(comp.toString());
	    
	    pane.setSpecial(true);
	    pane.add(comp);
	    if (comp instanceof JComponent)
	    	((JComponent)comp).setBorder(new DropShadowBorder());
	    container.add(pane);
		restoreSettings(oldValues);	    
	    return pane;
	}		
	protected Color getForeground() {
		return Color.black;
	}
	protected Color getBackground() {
		return leftPanel.getBackground();
	}
	protected void restoreSettings(Hashtable<String,Object> oldValues) {
		for (String key: oldValues.keySet()) UIManager.put(key,oldValues.get(key));
	}		
	protected Hashtable<String,Object> saveSettings(String[] keys) {
		Hashtable<String , Object> oldValues = new Hashtable<String, Object>();
		for (String key: keys) {
			if (UIManager.get(key)!=null)
			oldValues.put(key,UIManager.get(key));
		}
		return oldValues;
	}	

	@Override
	protected NanoPluginsManager createManager() {
	    PluginClassPath path;
	    try {
    	    path = new PluginClassPath();
    	    path.setPref_key("/ambitxt/nplugins");
    	    if (path.size()==0)
    	        path.add("ext");
    	    
    	    return new NanoPluginsManager(true,path.getPref_key()) {
    	    	@Override
    	    	public ImageIcon getLogo() {
    	    		return  Utils.createImageIcon("images/splash.png");
    	    	}
    	    	@Override
    	    	public URL getHelp() throws MalformedURLException {
    	    		try {
    	    			URL url = new URL("http://ambit.sourceforge.net/news.html");
    	    			url.getContent(); //verify if can read
    	    			return url; 
    	    		} catch (Exception x) {
    	    			
    	    			return  AmbitXT.class.getClassLoader().getResource("news.html");
    	    		}
    	    	}
    	    };
	    } catch (BackingStoreException x) {
	        return new NanoPluginsManager(false,null);
	    }
	    
	    
	}
	@Override
	protected Package getPackage() {
		Package pkg =  Package.getPackage("ambit2.ambitxt");
		Package[] pkgs = Package.getPackages();
		for (Package p : pkgs) {
			System.out.print("Package\t");
			System.out.println(p.getName());
			System.out.print(p.getImplementationTitle());
			System.out.println(p.getImplementationVersion());
		}
		return pkg;
	}
	@Override
	protected void addPlugins(NanoPluginsManager manager) {
	    if (manager.size()==0)
		try {
			manager.addPackage("ambit2.plugin.search.SearchPlugin","Simple search",
			        createImageIcon("images/search_16.png"));
			manager.addPackage("ambit2.plugin.analogs.AnalogsFinderPlugin","Find analogs",
			        createImageIcon("images/molecule_16.png"));
			manager.addPackage("ambit2.plugin.pbt.PBTCheckerPlugin","PBT Assessment",
			        createImageIcon("images/pill_16.png"));
			manager.addPackage("ambit2.plugin.dbtools.DBUtilityPlugin","Database tools",
			        createImageIcon("images/database_16.png"));
			manager.addPackage("ambit2.plugin.usermgr.UserManagerPlugin","Admin",
			        createImageIcon("images/user_16.png"));

		} catch (Exception x) {
            x.printStackTrace();
			logger.severe(x.getMessage());
		}		

	}
    public ImageIcon createImageIcon(String path) {
        try {
            java.net.URL imgURL = getClass().getClassLoader().getSystemResource(path);
    
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            } else {
                logger.warning("Couldn't find file: " + path);
                return null;
            }
        } catch (Exception x) {
            return null;
        }
    }	
	public static void main(final String[] args) {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
                new AmbitXT("Ambit XT",800,800,args);
            }
        });
   }
	@Override
	public String getTitle() {
	    return "Ambit XT";
	}
	
    public ImageIcon createImageIcon() {
    	return Utils.createImageIcon("images/ambit.png");
    }		
}

class AmbitTaskPaneContainerUI extends BasicTaskPaneContainerUI {
    protected LayoutManager createDefaultLayout() {
        return new VerticalLayoutUIResource(2);
    }
}

class AmbitPaneUI extends BasicTaskPaneUI {
	@Override
    protected Border createContentPaneBorder() {
        Color borderColor = UIManager.getColor("TaskPane.borderColor");
        return new CompoundBorder(new ContentPaneBorder(borderColor),
                BorderFactory.createEmptyBorder(0,0,0,0));
    }
	@Override
	protected Border createPaneBorder() {
		return new PaneBorder() {
		    protected void paintExpandedControls(JXTaskPane group, Graphics g, int x,
		    	      int y, int width, int height) {
		    	      ((Graphics2D)g).setRenderingHint(
		    	        RenderingHints.KEY_ANTIALIASING,
		    	        RenderingHints.VALUE_ANTIALIAS_ON);
		    	      
		    	      paintRectAroundControls(group, g, 0, y, width, height, Color.blue, Color.gray);
		    	      g.setColor(getPaintColor(group));
		    	      paintChevronControls(group, g, x, y, width, height);
		    	      
		    	      ((Graphics2D)g).setRenderingHint(
		    	        RenderingHints.KEY_ANTIALIASING,
		    	        RenderingHints.VALUE_ANTIALIAS_OFF);
		    	    }
	    
	        /**
	         * Paints oval 'border' area around the control itself.
	         * 
	         * @param group
	         *            Expanded group.
	         * @param g
	         *            Target graphics.
	         * @param x
	         *            X coordinate of the top left corner.
	         * @param y
	         *            Y coordinate of the top left corner.
	         * @param width
	         *            Width of the box.
	         * @param height
	         *            Height of the box.
	         */
	        protected void paintRectAroundControls(JXTaskPane group, Graphics g,
	                int x, int y, int width, int height, Color highColor,
	                Color lowColor) {
	            if (mouseOver) {
	                int x2 = x + 2 + width / 2;
	                int y2 = y + height;
	                g.setColor(highColor);
	                g.drawLine(x, y, x2, y);
	                g.drawLine(x, y, x, y2);
	                g.setColor(lowColor);
	                g.drawLine(x2, y, x2, y2);
	                g.drawLine(x, y2, x2, y2);
	            }
	        }		    
		    protected void paintChevronControls(JXTaskPane group, Graphics g,
		            int x, int y, int width, int height) {
		    	CollapsedIcon chevron;

		        if (group.isCollapsed()) {
		            chevron = new CollapsedIcon(false);
		        } else {
		            chevron = new CollapsedIcon(true);
		        }

		        int chevronX =  1 ; //chevron.getIconWidth() / 2;
		        int chevronY = y + (height / 2 - chevron.getIconHeight());
		        chevron.paintIcon(group, g, chevronX, chevronY);
		    }		    
   

	};
	}
	
    /**
     * Toggle icon.
     */
    protected static class CollapsedIcon implements Icon {
        boolean up = true;

        public CollapsedIcon(boolean up) {
            this.up = up;
        }

        public int getIconHeight() {
            return 3;
        }

        public int getIconWidth() {
            return 6;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (up) {
                g.drawLine(x, y+3, x+6, y+3);
            } else {
                g.drawLine(x, y+3, x+6, y+3);
                g.drawLine(x + 3, y, x + 3, y + 6);
            }
        }
    }	

}
