/**
 * Created on 2005-1-18
 *
 */
package ambit2.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.BitSet;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesGenerator;

import ambit2.smiles.SmilesParserWrapper;


/**
 * This is to test why fingerprint calculation give different results when application is invoked by Jawa Web Start
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class FingerprinterJWSTestApp implements ActionListener {
	
    final static String LOOKANDFEEL = "System";
    protected String caption = "Application";
    
    protected JFrame mainFrame = null;
    protected JPanel mainPanel = null;
    protected int h=360; 
    protected int w = 360;
    protected Fingerprinter fingerprinter;

    //GUI
    JTextArea fpArea;
    JComboBox cbox;
    //Data
    IMolecule molecule = null;
    SmilesParserWrapper sp = null;
    SmilesGenerator gen = null;

	public static final String[] SmilesToTest = {
		"NC1=CC2=C(C=C1)C3=C(C2)C=C(Br)C=C3",
		"COC1=CC=C(C)C=C1N",
		"NC1=CC=CC2=C1C=CC=N2",
		"CCOC1=CC=C(N)C=C1",
		"NC1=C2C=CC=CC2=CC=C1",
		"NC1=C2C(CC3=CC=CC=C23)=CC=C1",
		"NC1=CC=C2C=C3C=CC=CC3=CC2=C1",
		"NC1=CC=C2C=C3C=CC=CC3=CC2=C1",
		"NC1=CC=CC2=C1N=CC=C2",
		"NC1=CC2=NC3=CC=CC(N)=C3N=C2C=C1",
		"NC1=CC=C2C=CC=CC2=C1",
		"NC1=CC2=CC=CC3=CC=C4C=CC=C1C4=C23",
		"NC1=CC(=CC=C1)C2=CC=CC(=C2)[N+]([O-])=O",
		"CC1=CC(C)=C(N)C=C1C",
		"NC1=CC2=C(CC3=C2C=CC=C3)C=C1",
		"NC1=C(Cl)C=C(C=C1)C2=CC(Cl)=C(N)C=C2",
		"CC1=CC(C)=C(N)C=C1",
		"NC1=CC=C2C(CC3=CC(N)=CC=C23)=C1",
		"NC1=CC=C2C3=CC=CC=C3C4=CC=CC1=C24",
		"NC1=CC2=C(C=C1)C3=C(C2)C=CC=C3",
		"NC1=CC=CC=C1C2=CC=C(C=C2)[N+]([O-])=O",
		"NC1=CC=C(C=C1)C2=CC=CC=C2",
		"COC1=C(C)C=CC(N)=C1",
		"NC1=CC2=C(C=C1)C3=CC=CC=C3N2",
		"NC1=C(O)C=C(C=C1)[N+]([O-])=O",
		"NC1=CC=CC=C1C2=CC=CC=C2N",
		"NC1=CC2=C(C=C1)C3=C(C2)C=C(O)C=C3",
		"NC1=C2C=CC3=C(C=CC=C3)C2=CC=C1",
		"CC1=CC(N)=C(C)C=C1",
		"NC1=CC=C(C=C1)C2=CC=CC=C2[N+]([O-])=O",
		"CC1=CC(N)=C(O)C=C1",
		"NC1=CC2=NC3=CC=CC=C3N=C2C=C1",
		"NC1=CC=C(S)C=C1",
		"NC1=C(C=C(C=C1)[N+]([O-])=O)[N+]([O-])=O",
		"CC(C)C1=CC=C(N)C=C1N",
		"NC1=CC=C(F)C=C1F",
		"NC1=CC=C(CC2=CC=C(N)C=C2)C=C1",
		"CC1=C(N)C=CC(=C1)C2=CC(C)=C(N)C=C2",
		"NC1=CC2=C3C(=CC=C2)C4=CC=CC=C4C3=C1",
		"NC1=CC=CC=C1C2=CC=CC(=C2)[N+]([O-])=O",
		"NC1=C2C3=C(C=CC=C3)C4=CC=CC(C=C1)=C24",
		"NC1=CC=C(CCC2=CC=C(N)C=C2)C=C1",
		"NC1=CC=C(Cl)C=C1",
		"NC1=CC=C2C(C=CC3=C2C=CC=C3)=C1",
		"NC1=CC=C(F)C=C1",
		"NC1=CC2=C(C=CC=C2)C3=C1C=CC=C3",
		"NC1=CC=CC(=C1)C2=CC=CC(N)=C2",
		"NC1=CC2=CC=C3C=CC=C4C=CC(=C1)C2=C34",
		"NC1=CC(Cl)=C(N)C(Cl)=C1",
		"CC(=O)NC1=CC=C2C(CC3=CC(N)=CC=C23)=C1",
		"NC1=CC=C2N=C3C=CC(N)=CC3=NC2=C1",
		"NC1=CC2=C(C=C1)N=CC=C2",
		"COC1=CC(C)=C(N)C=C1",
		"NC1=CC(=CC=C1)C2=CC=CC=C2[N+]([O-])=O",
		"NC1=CC=C(C=C1)C2=C(N)C=CC=C2",
		"NC1=CC=CC2=NC3=C(N)C=CC=C3N=C12",
		"NC1=CC=C(SSC2=CC=CC=C2)C=C1",
		"NC1=C(C=C(C=C1Br)[N+]([O-])=O)[N+]([O-])=O",
		"CCCCC1=C(N)C=C(N)C=C1",
		"NC1=CC=C(OC2=CC=C(N)C=C2)C=C1",
		"NC1=C(C=CC=C1)C2=CC=CC=C2",
		"NC1=CC=CC2=NC3=CC=CC(N)=C3N=C12",
		"NC1=CC=CC2=C1CC3=C2C=CC=C3",
		"NC1=CC=C2C3=CC=CC4=C3C(=CC=C4)C2=C1",
		"NC1=C(Cl)C=CC=C1",
		"NC1=CC=C(C=C1)C(F)(F)F",
		"NC1=C(C2=C(C=CC=C2)C=C1)[N+]([O-])=O",
		"NC1=CC(=CC=C1)C2=CC=C(C=C2)[N+]([O-])=O",
		"NC1=CC=C(Br)C=C1",
		"NC1=CC(Cl)=CC=C1O",
		"COC1=C(N)C=CC(=C1)C2=CC(OC)=C(N)C=C2",
		"NC1=CC=C(C=C1)C2CCCCC2",
		"NC1=CC=C(OC2=CC=CC=C2)C=C1",
		"CCC1=CC(CC2=CC(CC)=C(N)C=C2)=CC=C1N",
		"NC1=CC=C2C(CC3=C2C=CC(=C3)[N+]([O-])=O)=C1",
		"NC1=CC=C(C=C1)C2=CC=C(N)C=C2",
		"NC1=C2C=CC=CC2=C(C=C1)[N+]([O-])=O",
		"NC1=CC=C(C=C1)C2=CC=CC(=C2)[N+]([O-])=O",
		"NC1=CC=C(C=C1)C2=CC=C(C=C2)[N+]([O-])=O",
		"NC1=CC=CC2=NC3=CC=CC=C3N=C12",
		"NC1=C(F)C=C(CC2=CC(F)=C(N)C=C2)C=C1",
		"NC1=C(C=C(Cl)C=C1)[N+]([O-])=O",
		"NC1=CC2=C(C=CC=C2)N=C1",
		"NC1=CC=C2NC3=C(C=CC=C3)C2=C1",
		"NC1=C(N)C=C(Cl)C=C1",
		"NC1=CC=C2C=CC3=C(C=CC=C3)C2=C1",
		"NC1=CC=C(C=C1)C2=CC=CC(N)=C2",
		"NC1=CC=CC2=C1NC3=C2C=CC=C3"
	};
   
	/**
	 * 
	 */
	public FingerprinterJWSTestApp(String title) {
	    super();
	    fingerprinter = new Fingerprinter(1024);
		caption = title;
        mainFrame = new JFrame(title);
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.setSize(new Dimension(w,h));
        mainFrame.addWindowListener( new WindowAdapter() {
        	   public void windowClosing( WindowEvent e ){
        	     if (canClose()) doClose();  
        	   }
        	   } ); 
        JMenuBar menuBar = new JMenuBar();
		menuBar.setPreferredSize(new Dimension(200,18));
		//JMenu menu = new JMenu("Java info");
		JMenuItem menuItem = new JMenuItem("JavaInfo");
		menuItem.addActionListener(new ActionListener() {
			/* (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				showJavaInfo();

			}
		});
				
		menuBar.add(menuItem);
		
        //Create and set up the panel.
        mainPanel = new JPanel();
        
        createWidgets(mainFrame,mainPanel);
        
        Container ct = mainFrame.getContentPane();
        ct.add(menuBar,BorderLayout.NORTH);
        ct.add(mainPanel,BorderLayout.CENTER);
        //mainFrame.setContentPane(mainPanel);
        
    	centerScreen();
        mainFrame.pack();
        mainFrame.setVisible(true);        
	}

    protected static void createAndShowGUI() {
        initLookAndFeel();
        JFrame.setDefaultLookAndFeelDecorated(true);
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
    public void actionPerformed(ActionEvent event) {
    	
    }
    protected void createWidgets(JFrame aFrame, JPanel aPanel) {
    	aPanel.setLayout(new BorderLayout());
    	cbox = new JComboBox(SmilesToTest);
    	cbox.setSelectedIndex(0);
    	cbox.addItemListener(new ItemListener() {
    		/* (non-Javadoc)
			 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
			 */
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (sp == null) sp = SmilesParserWrapper.getInstance();
					String smiles = e.getItem().toString();
					try {
						molecule = sp.parseSmiles(getCanonical(smiles));
						createFP();
					} catch (InvalidSmilesException x) {
						molecule = null;
					}
					
					//SmilesGenerator gen = new SmilesGenerator();
					//textField.setText(gen.createSMILES(molecule));
					}
			}
    	});
    	aPanel.add(cbox,BorderLayout.NORTH);
    	
    	fpArea = new JTextArea();
    	fpArea.setEditable(false);
    	fpArea.setPreferredSize(new Dimension(200,200));
    	fpArea.setBorder(BorderFactory.createEtchedBorder(Color.red,Color.blue));
    	fpArea.setToolTipText("The fingerprint is displayed here");
    	aPanel.add(fpArea,BorderLayout.CENTER);
    	
    	JButton getIt = new JButton(new AbstractAction("Create All Fingerprints") {
    		/* (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				if (sp == null) sp = SmilesParserWrapper.getInstance();
				StringBuffer b = new StringBuffer();
				b.append("No\tSMILES\tCardinality\tBitSet\n");
			
				for (int i=0; i < SmilesToTest.length; i++) {
					String s = getCanonical(SmilesToTest[i]);
					//String s = SmilesToTest[i];
					b.append((i+1));
					b.append('\t');
					b.append(s);
					b.append('\t');
					
					try {
						molecule = sp.parseSmiles(s);
						BitSet bs = fingerprinter.getFingerprint((AtomContainer) molecule);
						b.append(bs.cardinality());
						b.append("\t\"");
						b.append(bs);
						b.append("\"");
					} catch (InvalidSmilesException x) {
						b.append(x.getMessage());
					} catch (NoSuchAtomException x) {
						b.append(x.getMessage());
					} catch (Exception x) {
					    b.append(x.getMessage());
					}
					
					b.append('\n');
				}
				fpArea.setText(b.toString());
			}
    	});
    	
    	aPanel.add(getIt,BorderLayout.SOUTH);
    }
    protected void createFP() {
		if (molecule != null) 
			try {
				BitSet bs = fingerprinter.getFingerprint((AtomContainer) molecule);
				StringBuffer b = new StringBuffer();
				b.append("Item\t");
				b.append((cbox.getSelectedIndex()+1));
				b.append('\n');
				b.append("Fingerprint cardinality\t");
				b.append(bs.cardinality());
				b.append('\n');
				b.append(bs.toString());
				b.append('\n');				
				fpArea.setText(b.toString());
			} catch (NoSuchAtomException x) {
				fpArea.setText(x.getMessage());
			} catch (Exception xx) {
			    fpArea.setText(xx.getMessage());
			}
			
		else fpArea.setText("");
    }
    public String getCanonical(String smiles) {
    	if (gen == null) gen = new SmilesGenerator();
		try {
			//parse the smiles to get the Molecule
			IMolecule mol1 = sp.parseSmiles(smiles);
			//create canonical smiles from the molecule 
			return gen.createSMILES(mol1);
		} catch (InvalidSmilesException x) {
			System.err.println("Invalid SMILES \t" + smiles);
			return "";
		}
    }
	public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
                new FingerprinterJWSTestApp("Fingerprinter JWS test");                
            }
        });
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
		String s = "";
		for (int i = 0; i < props.length; i++) {
			b.append(props[i]);
			b.append(" = ");
			try {
				s = System.getProperty(props[i]);
				if (s == null) {
					b.append("NA\n");				
					continue;
				}
			} catch (Exception x) {
				s = x.getMessage();
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
	
}