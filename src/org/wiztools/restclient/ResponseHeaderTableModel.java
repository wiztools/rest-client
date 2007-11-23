/*
 * ResponseHeaderTableModel.java
 * 
 * Created on Nov 21, 2007, 4:07:13 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import javax.swing.table.AbstractTableModel;
import org.apache.commons.httpclient.Header;

/**
 *
 * @author schandran
 */
public class ResponseHeaderTableModel extends AbstractTableModel {
    
    private Header[] headers;
    
    public void setHeader(Header[] headers){
        this.headers = headers;
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
            return headers[row].getName();
        }
        else{
            return headers[row].getValue();
        }
    }

}
