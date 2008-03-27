package ambit2.data.qmrf;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;


public class QMRFSubChapterDateEditor extends QMRFSubChapterTextEditor {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8066777781821570866L;
	public QMRFSubChapterDateEditor(QMRFSubChapterDate chapter, boolean editable) {
        this(chapter,4);
        setEditable(editable);
    }
    public QMRFSubChapterDateEditor(QMRFSubChapterDate chapter, int indent) {
        super(chapter,indent);
    }

    @Override
    protected JComponent createTextComponent() {
    	textPane = null;
    	textField = new JFormattedTextField(); //new SimpleDateFormat("yyyy-M-d"));
    	if (!isEditable()) textField.setBackground(disabled);
    	textField.setEditable(isEditable());
    	textField.addFocusListener(this);
    	textField.setToolTipText(chapter.getWrappedHelp(150));
    	textField.setText(((QMRFSubChapterDate)chapter).getText());
    	Dimension d = new Dimension(Integer.MAX_VALUE,24);
    	textField.setPreferredSize(d);
    	textField.setMaximumSize(d);
        textField.setMinimumSize(new Dimension(256,128));   
        return textField;
    }

	protected JComponent[] createJComponents() {
		textField = new JFormattedTextField(); 
    	if (!isEditable()) textField.setBackground(disabled);
    	textField.setEditable(isEditable());		
    	textField.addFocusListener(this);
    	textField.setToolTipText(chapter.getWrappedHelp(150));
    	textField.setText(((QMRFSubChapterDate)chapter).getText());
    	Dimension d = new Dimension(200,20);
    	textField.setPreferredSize(d);
    	textField.setMaximumSize(d);
        textField.setMinimumSize(d);      	
		return new JComponent[] {textField};
	}    
}
