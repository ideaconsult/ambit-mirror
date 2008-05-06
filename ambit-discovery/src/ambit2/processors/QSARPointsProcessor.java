package ambit2.processors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.openscience.cdk.interfaces.IChemObject;

import ambit2.config.AmbitCONSTANTS;
import ambit2.ui.actions.dbadmin.DbBatchImportQSARModels;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.ui.editors.ModelEditor;
import ambit2.data.descriptors.AmbitColumnType;
import ambit2.data.model.PredictedValue;
import ambit2.data.molecule.MolProperties;
import ambit2.exceptions.AmbitException;

/**
 * To be used by {@link DbBatchImportQSARModels}
 * @author Nina Jeliazkova
 *
 */
public class QSARPointsProcessor extends DefaultAmbitProcessor {
	protected MolProperties properties;

	public QSARPointsProcessor(MolProperties properties) {
		super();
		this.properties = properties;
	}

	public QSARPointsProcessor() {
		super();
	}
	
	public MolProperties getProperties() {
		return properties;
	}

	public void setProperties(MolProperties properties) {
		this.properties = properties;
		properties.getQsarModel().setDescriptors(properties.getDescriptorsList());
	}
	public IAmbitEditor getEditor() {
		return new QSARParserEditor(this);
	}
	public Object process(Object object) throws AmbitException {
		if (object instanceof IChemObject)  {
			IChemObject co = (IChemObject) object;
			if (properties == null) throw new AmbitException("Can't prepare qsar points, undefined properties");
			Enumeration e = properties.getQSAR().keys();
			while (e.hasMoreElements()) {
				Object key = e.nextElement();
				Object value = properties.getQSAR().get(key);
				switch (((AmbitColumnType) value).getId()) {
				case (AmbitColumnType._ctYpredicted): {
					co.setProperty(AmbitCONSTANTS.PREDICTED, 
							new PredictedValue(co.getProperty(key),properties.getQsarModel()));
					break;
				}
				
				}
			}
		}
		return object;
	}
	public String toString() {
		return "Extracts QSAR point";
	}
}
	
class QSARParserEditor extends JPanel implements IAmbitEditor {
	public QSARParserEditor(QSARPointsProcessor parser) {
		super(new BorderLayout());
		if ((parser.properties != null) && (parser.properties.getQsarModel()!=null)) {
			ModelEditor le = new ModelEditor(parser.properties.getQsarModel());
			add(le,BorderLayout.CENTER);
		}
	}
	public JComponent getJComponent() {
		return this;
	}
	public boolean view(Component parent, boolean editable, String title) throws AmbitException {
		return
		JOptionPane.showConfirmDialog(parent, getJComponent(),"Experimental data",
				JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION;
		
	}
	public void setEditable(boolean editable) {
		// TODO Auto-generated method stub
		
	}
	public boolean isEditable() {
		// TODO Auto-generated method stub
		return true;
	}
}
