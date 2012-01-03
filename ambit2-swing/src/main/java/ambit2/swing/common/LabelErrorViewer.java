package ambit2.swing.common;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class LabelErrorViewer extends ErrorViewer<JLabel> {
	protected ImageIcon warning;
	protected JLabel createComponent() {
		warning = UITools.createImageIcon("ambit/ui/images/warning.png");
		JLabel label = new JLabel();
        Dimension box = new Dimension(32,32);
        label.setPreferredSize(box);
        label.setMaximumSize(box);
        label.setMinimumSize(box);
        label.setOpaque(true);
        label.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mousePressed(MouseEvent e) {
        		showErrorlog(theError);
        	}
        });		
        return label;
	}
    protected void update() {
    	if (theError != null) {
    		errorsLabel.setText(null);
    		errorsLabel.setIcon(warning);
    		errorsLabel.setToolTipText("Click here for error details");
    	} else { 
    		errorsLabel.setText("");
    		errorsLabel.setIcon(null);
    		errorsLabel.setToolTipText("");
    	}	
    }    
	
}
