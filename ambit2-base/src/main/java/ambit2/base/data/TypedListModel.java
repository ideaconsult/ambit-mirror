package ambit2.base.data;

import javax.swing.ListModel;

public interface TypedListModel<P> extends ListModel  {
	P getElementAt(int index);
}
