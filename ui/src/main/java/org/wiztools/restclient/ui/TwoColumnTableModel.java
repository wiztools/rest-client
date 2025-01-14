package org.wiztools.restclient.ui;

import javax.swing.table.AbstractTableModel;
import org.wiztools.commons.MultiValueMap;

/**
 *
 * @author schandran
 */
public class TwoColumnTableModel extends AbstractTableModel {

    private final String[] colNames;
    private Object[][] data = new String[0][0];
    
    public TwoColumnTableModel(final String[] colNames){
        if(colNames.length != 2){
            throw new IllegalArgumentException("The length of array should be equal to 2.");
        }
        this.colNames = colNames;
    }
    
    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    @Override
    public String getColumnName(int col) {
        return colNames[col];
    }
    
    @Override
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex){
        return true;
    }
    
    public void insertRow(Object key, Object value){
        int len = data.length;
        Object[][] t = new Object[len+1][2];
        t[0][0] = key;
        t[0][1] = value;
        for(int i=1; i<len+1; i++){
            System.arraycopy(data[i-1], 0, t[i], 0, 2);
        }
        data = null;
        data = t;
        fireTableDataChanged();
    }
    
    public void deleteRow(int row){
        int len = data.length;
        Object[][] t = new Object[len-1][2];
        boolean passedDeletionRow = false;
        for(int i=0; i<len; i++){
            if(i == row){
                passedDeletionRow = true;
                continue;
            }
            for(int j=0; j<2; j++){
                if(!passedDeletionRow){
                    t[i][j] = data[i][j];
                }
                else{
                    t[i-1][j] = data[i][j];
                }
            }
        }
        data = null;
        data = t;
        fireTableDataChanged();
    }
    
    public void setData(final MultiValueMap<String, String> dataMap){
        Object[][] o = new Object[dataMap.values().size()][2];

        int i = 0;
        for(String key: dataMap.keySet()) {
            for(String value: dataMap.get(key)) {
                o[i][0] = key;
                o[i][1] = value;
                i++;
            }
        }
        data = null;
        data = o;
        fireTableDataChanged();
    }
    
    public Object[][] getData(){
        return data;
    }
}
