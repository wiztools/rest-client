/*
 * ResponseHeaderTableModel.java
 * 
 * Created on Nov 21, 2007, 4:07:13 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import java.util.Map;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author schandran
 */
public class ResponseHeaderTableModel extends AbstractTableModel {
    
    private String[][] headers;
    
    public void setHeader(Map<String, String> mapHeaders){
        if(mapHeaders == null){
            headers = new String[0][0];
        }
        else{
            headers = new String[mapHeaders.size()][2];
            int i = 0;
            for(String key: mapHeaders.keySet()){
                headers[i][0] = key;
                headers[i][1] = mapHeaders.get(key);
                i++;
            }
        }
        fireTableDataChanged();
    }
    
    @Override
    public String getColumnName(int col) {
        if(col == 0){
            return "HTTP Header";
        }
        else{
            return "Value";
        }
    }

    public int getRowCount() {
        if(headers == null){
            return 0;
        }
        return headers.length;
    }

    public int getColumnCount() {
        // Key and Value
        return 2;
    }

    public Object getValueAt(int row, int column) {
        // 0 means key
        if(column == 0){
            return headers[row][0];
        }
        else{
            return headers[row][1];
        }
    }

}
