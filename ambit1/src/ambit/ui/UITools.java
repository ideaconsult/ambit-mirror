/*
 * Created on 2006-2-24
 *
 */
package ambit.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.JPopupMenu.Separator;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;

import ambit.log.AmbitLogger;
import ambit.ui.actions.InsertSymbolAction;

/**
 * static finctions for icon loading and browser launch. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-2-24
 */
public class UITools {
	static AmbitLogger logger = new AmbitLogger(UITools.class);

    /**
     * 
     */
    protected UITools() {
        super();
    }
    public static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = UITools.class.getClassLoader().getResource(path);

        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            logger.error("Couldn't find file: " + path);
            return null;
        }
    }	
    /**
     * Launches a browser with specified url
     * @param url
     */
    public static void openURL(String url) {
        String osName = System.getProperty("os.name");
        try {
           if (osName.startsWith("Mac OS")) {
              Class fileMgr = Class.forName("com.apple.eio.FileManager");
              Method openURL = fileMgr.getDeclaredMethod("openURL",
                 new Class[] {String.class});
              openURL.invoke(null, new Object[] {url});
              }
           else if (osName.startsWith("Windows"))
              Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
           else { //assume Unix or Linux
              String[] browsers = {
                 "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
              String browser = null;
              for (int count = 0; count < browsers.length && browser == null; count++)
                 if (Runtime.getRuntime().exec(
                       new String[] {"which", browsers[count]}).waitFor() == 0)
                    browser = browsers[count];
              if (browser == null)
                 throw new Exception(browsers.toString());
              else
                 Runtime.getRuntime().exec(new String[] {browser, url});
              }
           }
        catch (Exception e) {
           JOptionPane.showMessageDialog(null, e.getMessage() + ":\n" + e.getLocalizedMessage());
           }
 
     }

	public static JMenu createEditMenu(Container mainFrame) {
		JMenu mainMenu = new JMenu("Edit");
		mainMenu.setMnemonic(KeyEvent.VK_E);
		JMenuItem menuItem;
		menuItem = new JMenuItem(new DefaultEditorKit.CutAction());
		menuItem.setIcon(UITools.createImageIcon("ambit/ui/images/cut.png"));
		menuItem.setText("Cut (Ctr-T)");
		menuItem.setMnemonic(KeyEvent.VK_T);
		mainMenu.add(menuItem);
		menuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
		menuItem.setText("Copy (Ctrl-C)");
		menuItem.setMnemonic(KeyEvent.VK_C);
		mainMenu.add(menuItem);
		menuItem = new JMenuItem(new DefaultEditorKit.PasteAction());
		menuItem.setIcon(UITools.createImageIcon("ambit/ui/images/paste_plain.png"));
		menuItem.setText("Paste (Ctrl-V)");
		menuItem.setMnemonic(KeyEvent.VK_P);
		mainMenu.add(menuItem);
		
		mainMenu.add(new Separator());
		menuItem = new JMenuItem(new InsertSymbolAction(mainFrame,"Insert symbol"));
		menuItem.setIcon(UITools.createImageIcon("ambit/ui/images/text_letter_omega.png"));
		menuItem.setText("Insert symbol");
		menuItem.setMnemonic(KeyEvent.VK_P);
		mainMenu.add(menuItem);
		
		return mainMenu;
	}
	
	public static JMenu createStyleMenu() {
        JMenu menu = new JMenu("Style");

        
        Action action = new StyledEditorKit.BoldAction();
        action.putValue(Action.SMALL_ICON, UITools.createImageIcon("ambit/ui/images/text_bold.png"));
        action.putValue(Action.NAME, "Bold");
        menu.add(new JCheckBoxMenuItem(action));

        action = new StyledEditorKit.ItalicAction();
        action.putValue(Action.SMALL_ICON, UITools.createImageIcon("ambit/ui/images/text_italic.png"));
        action.putValue(Action.NAME, "Italic");
        menu.add(new JCheckBoxMenuItem(action));

        action = new StyledEditorKit.UnderlineAction();
        action.putValue(Action.SMALL_ICON, UITools.createImageIcon("ambit/ui/images/text_underline.png"));
        action.putValue(Action.NAME, "Underline");
        menu.add(new JCheckBoxMenuItem(action));
        
        

        menu.addSeparator();
        ButtonGroup g = new ButtonGroup();
        JRadioButtonMenuItem mb0 = new JRadioButtonMenuItem(new NormalScriptAction());
        mb0.setSelected(true);
        JRadioButtonMenuItem mb1 = new JRadioButtonMenuItem(new SubscriptAction());
        JRadioButtonMenuItem mb2 = new JRadioButtonMenuItem(new SuperscriptAction());
        g.add(mb0);g.add(mb1);g.add(mb2);
        menu.add(mb0);
        menu.add(mb1);
        menu.add(mb2);

        menu.addSeparator();

        //Font size
        Object[][] fontssize = {{"12",12},{"14",14},{"18",18}};
        
        g = new ButtonGroup();
        ImageIcon f = UITools.createImageIcon("ambit/ui/images/style.png");
        for (int i=0; i < fontssize.length; i++) {
        	Action a = new StyledEditorKit.FontSizeAction((String)fontssize[i][0],(Integer)fontssize[i][1]);
        	a.putValue(Action.SMALL_ICON, f);
        	JRadioButtonMenuItem b = new JRadioButtonMenuItem(a);
        	b.setSelected(i==0);
        	g.add(b);
        	menu.add(b);
        }

        
        menu.addSeparator();
        //Fonts
        Object[][] fonts = {{"Serif"},{"SansSerif"}};
        
        g = new ButtonGroup();
        for (int i=0; i < fonts.length; i++) {
        	Action a = new StyledEditorKit.FontFamilyAction(
        			(String)fonts[i][0],(String)fonts[i][0]
            );
        	a.putValue(Action.SMALL_ICON, f);
        	JRadioButtonMenuItem b = new JRadioButtonMenuItem(a);
        	b.setSelected(i==0);
        	g.add(b);
        	menu.add(b);
        }
        menu.addSeparator();
        
        //Colors
        Object[][] clr = {
        		{"Black",Color.black},
        		{"Red",Color.red},
        		{"Green",Color.green},
        		{"Blue",Color.blue}
        };
        
        g = new ButtonGroup();
        for (int i=0; i < clr.length; i++) {
        	JRadioButtonMenuItem b = new JRadioButtonMenuItem(new StyledEditorKit.ForegroundAction(
        			(String)clr[i][0],(Color)clr[i][1]
        			                 )
        	);
        	b.setSelected(i==0);
        	g.add(b);
        	menu.add(b);
                    
        }
        return menu;
    }
	
	public static JComponent createToolbar(int width, int height) {
	    JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL  ) ;
        toolBar.setPreferredSize(new Dimension(width,  height));
        toolBar.setFloatable(false);
        return toolBar;
	}	
}



class SubscriptAction extends StyledEditorKit.StyledTextAction{			
	public SubscriptAction(){			

		super("<html>x<sub>2</sub></html>"); //StyleConstants.Subscript.toString());
		putValue(SHORT_DESCRIPTION,"Subscript");
		putValue(Action.SMALL_ICON, UITools.createImageIcon("ambit/ui/images/text_subscript.png"));
	}		
	public void actionPerformed(ActionEvent ae){			
		JEditorPane editor = getEditor(ae);			
		if (editor != null) {				
			StyledEditorKit kit = getStyledEditorKit(editor);				
			MutableAttributeSet attr = kit.getInputAttributes();				
			SimpleAttributeSet sas = new SimpleAttributeSet();				
			StyleConstants.setSubscript(sas, true);				
			setCharacterAttributes(editor, sas, false);			
		}		
	}	
}	

class NormalScriptAction extends StyledEditorKit.StyledTextAction{			
	public NormalScriptAction(){			
		super("<html>x</html>"); 
		putValue(SHORT_DESCRIPTION,"Normal script");
		putValue(Action.SMALL_ICON, UITools.createImageIcon("ambit/ui/images/font.png"));

	}		
	public void actionPerformed(ActionEvent ae){			
		JEditorPane editor = getEditor(ae);			
		if (editor != null) {				
			StyledEditorKit kit = getStyledEditorKit(editor);				
			MutableAttributeSet attr = kit.getInputAttributes();				
			
			SimpleAttributeSet sas = new SimpleAttributeSet();				
			StyleConstants.setSubscript(sas, false);			
			StyleConstants.setSuperscript(sas, false);	
			setCharacterAttributes(editor, sas, false);			
		}		
	}	
}		

class SuperscriptAction extends StyledEditorKit.StyledTextAction{			
	public SuperscriptAction(){			
		super("<html>x<sup>2</sup></html>"); //StyleConstants.Superscript.toString());
		putValue(SHORT_DESCRIPTION,"Superscript");
		putValue(Action.SMALL_ICON, UITools.createImageIcon("ambit/ui/images/text_superscript.png"));
	}		
	public void actionPerformed(ActionEvent ae){			
		JEditorPane editor = getEditor(ae);			
		if (editor != null) {				
			StyledEditorKit kit = getStyledEditorKit(editor);				
			MutableAttributeSet attr = kit.getInputAttributes();				
			SimpleAttributeSet sas = new SimpleAttributeSet();				
			StyleConstants.setSuperscript(sas, true);				
			setCharacterAttributes(editor, sas, false);			
		}					
	}	
}
