/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author schandran
 */
public class RequestHeaderTableModel extends AbstractTableModel {

    private static final String[] colNames = new String[]{"HTTP Header", "Value"};
    private Object[][] data = new String[0][0];
    
    public int getRowCount() {
        return data.length;
    }

    public int getColumnCount() {
        return 2;
    }

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
    
    public void insertRow(Object key, Object value){
        int len = data.length;
        Object[][] t = new Object[len+1][2];
        t[0][0] = key;
        t[0][1] = value;
        for(int i=1; i<len+1; i++){
            for(int j=0; j<2; j++){
                t[i][j] = data[i-1][j];
            }
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
    
    public Object[][] getData(){
        return data;
    }
}
