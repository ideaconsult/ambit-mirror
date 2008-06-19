package ambit.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public abstract class ErrorViewer<T extends JComponent> {
    protected T errorsLabel;
	protected Exception theError;    

	public ErrorViewer() {
		errorsLabel = createComponent();
	}
	protected abstract T createComponent();

	public T getView() {
		return errorsLabel;
	}
    public void setError(Exception x) {
    	theError = x;
    	update();
    }
    protected abstract void update();

    
    public void showErrorlog(Exception x) {
    	if (x == null) return;
        JPanel p = new JPanel(new BorderLayout());
        

        JTextArea t;
        String top = x.toString();
        
        StringWriter w = new StringWriter();
        x.printStackTrace(new PrintWriter(w));
        t = new JTextArea(w.toString());
        w = null;        
       
        
        t.setAutoscrolls(true);
        
        JScrollPane sp = new JScrollPane(t);
        sp.setAutoscrolls(true);
        sp.setPreferredSize(new Dimension(350,200));
        p.add(sp,BorderLayout.CENTER);
        
        Object[] options = {"Hide",
                "Clear"
                };

        if (JOptionPane.showOptionDialog(errorsLabel,
                p,
                "Last error",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]) == 1) {
            setError(null);
        }   
    }	    
	
}
