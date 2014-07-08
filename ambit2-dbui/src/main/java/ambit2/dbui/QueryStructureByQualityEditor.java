package ambit2.dbui;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.QLabel;
import ambit2.base.data.QLabel.QUALITY;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.StringCondition;
import ambit2.db.search.structure.QueryStructureByQuality;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;

public class QueryStructureByQualityEditor extends
		QueryEditor<ISourceDataset, QLabel, StringCondition,IStructureRecord,QueryStructureByQuality> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5588226956876509820L;

	@Override
	protected JComponent createConditionComponent() {
		JComboBox box = BasicComponentFactory.createComboBox(
                new SelectionInList<StringCondition>(
                		new StringCondition[] {
                			StringCondition.getInstance(StringCondition.C_EQ),
                			StringCondition.getInstance(StringCondition.C_LIKE),
                			StringCondition.getInstance(StringCondition.C_NOTLIKE),
                			StringCondition.getInstance(StringCondition.C_REGEXP),
                			StringCondition.getInstance(StringCondition.C_SOUNDSLIKE),
                			StringCondition.getInstance(StringCondition.C_STARTS_WITH)
                		},
                		presentationModel.getModel("condition")));
		AutoCompleteDecorator.decorate(box);
		return box;
	}

	@Override
	protected JComponent createValueComponent() {
		JComboBox box = BasicComponentFactory.createComboBox(
                new SelectionInList<QLabel>(
                		new QLabel[] {
                			new QLabel(QUALITY.OK),
                			new QLabel(QUALITY.ProbablyOK),
                			new QLabel(QUALITY.Unknown),
                			new QLabel(QUALITY.ProbablyERROR),
                			new QLabel(QUALITY.ERROR)
                		},
                		presentationModel.getModel("value")));
		AutoCompleteDecorator.decorate(box);
		return box;
	}
	@Override
	protected JComponent createFieldnameComponent() {
		return new JLabel("Quality label");
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}
