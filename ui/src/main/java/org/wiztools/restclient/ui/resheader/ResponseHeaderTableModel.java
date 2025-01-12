package org.wiztools.restclient.ui.resheader;

import javax.swing.table.AbstractTableModel;
import org.wiztools.commons.MultiValueMap;

/**
 *
 * @author subwiz
 */
class ResponseHeaderTableModel extends AbstractTableModel {
    
    private final String[] title = new String[]{"HTTP Header", "Value"};
    private String[][] headers;
    
    public void setHeaders(MultiValueMap<String, String> mapHeaders){
        if(mapHeaders == null){
            headers = new String[0][0];
        }
        else{
            headers = new String[mapHeaders.values().size()][2];
            int i = 0;
            for(String key: mapHeaders.keySet()){
                for(String value: mapHeaders.get(key)) {
                    headers[i][0] = key;
                    headers[i][1] = value;
                    i++;
                }
            }
        }
        fireTableDataChanged();
    }
    
    public String[][] getHeaders(){
        return headers;
    }
    
    @Override
    public String getColumnName(int col) {
        return title[col];
    }

    @Override
    public int getRowCount() {
        if(headers == null){
            return 0;
        }
        return headers.length;
    }

    @Override
    public int getColumnCount() {
        // Key and Value
        return 2;
    }

    @Override
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
