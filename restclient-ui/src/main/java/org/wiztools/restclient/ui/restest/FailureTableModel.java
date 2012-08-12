package org.wiztools.restclient.ui.restest;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.wiztools.restclient.TestExceptionResult;

/**
 *
 * @author subwiz
 */
class FailureTableModel extends AbstractTableModel {

    private Object[] failures;

    public void setData(List<TestExceptionResult> failures) {
        if (failures != null) {
            this.failures = failures.toArray();
        }
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int col) {
        if (col == 0) {
            return "Message";
        } else {
            return "Line";
        }
    }

    @Override
    public int getRowCount() {
        if (failures == null) {
            return 0;
        }
        return failures.length;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TestExceptionResult bean = (TestExceptionResult) failures[rowIndex];
        if (columnIndex == 0) {
            return bean.getExceptionMessage();
        } else {
            return bean.getLineNumber();
        }
    }
}
