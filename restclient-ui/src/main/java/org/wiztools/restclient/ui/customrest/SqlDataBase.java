package org.wiztools.restclient.ui.customrest;

import org.wiztools.restclient.ui.RESTView;
import org.wiztools.restclient.util.ConfigUtil;
import org.wiztools.restclient.util.Util;

import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 数据库操作接口，写入
 * created by 10192065 on 2017/8/31
 * User: 10192065(yzg)
 * Date: 2017/8/31
 */
public class SqlDataBase {
    private static final Logger LOG = Logger.getLogger(SqlDataBase.class.getName());
    private static final String TABLENAME = "recode_table";

    private File recodeFile = ConfigUtil.getConfigFile("admadebug.db");
    private String dbFile = recodeFile.getAbsolutePath();
    private Connection conn = null;
    Statement stmt = null;
    RESTView view = null;

    SqlDataBase(final RESTView view) {
        initDatabase(view);
    }

    private void initDatabase(final RESTView view) {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:"+ dbFile);
            stmt = conn.createStatement();
        } catch ( Exception e ) {
            LOG.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage(), e);
            view.showError(Util.getStackTrace(e));
        }
        LOG.log(Level.INFO,"Opened database successfully");
        createTable(view);
    }

    private void createTable(final RESTView view) {
        try {
            String selectSql = "select * from " + TABLENAME + " limit 1;";
            query(selectSql);
        } catch ( Exception e ) {
            String createSql = "create table if not exists [" + TABLENAME +
                    "]([id] TEXT, [value] TEXT, [date] datetime default (getdate()), [info] TEXT, [comment] TEXT);";
            execute(createSql);
            LOG.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage(), e);
//            view.showError(Util.getStackTrace(e));
        }
        LOG.log(Level.INFO,"create or find table successfully");
    }


    private void execute(String sql) {

        try {
            stmt.executeUpdate(sql);
        } catch(Exception e) {
            view.showError(Util.getStackTrace(e));
        }
    }

    private ResultSet query(String sql) {
        ResultSet result = null;
        try {
            result = stmt.executeQuery(sql);
        } catch(Exception e) {
            view.showError(Util.getStackTrace(e));
        }
        return result;
    }

    private String getDateString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new java.util.Date());
    }

    public void insertIpRecode(String ipAddress) {
        String time = getDateString();
        // 首先要判断该IP是否存在，存在则更新时间，否则插入
        String sqlStr = "SELECT value FROM " + TABLENAME + " where id='IP' and value='" + ipAddress + "';";
        ResultSet rs = query(sqlStr);
        try{
            if (rs.next()) {
                sqlStr = "UPDATE " + TABLENAME + " set date='" +time +"' where id='IP' and value='" +
                        ipAddress + "';";
            }
            else {
                sqlStr = "INSERT INTO " + TABLENAME + " (id, value, date, info, comment) " +
                        "VALUES ('IP', '" + ipAddress + "', '" + time +"', '', '');";
            }
            execute(sqlStr);
        } catch(Exception e) {
            view.showError(Util.getStackTrace(e));
        }
    }

    public List<String> getIpRecode() {
        List<String> ipList = new ArrayList();
        String sqlStr = "SELECT value FROM " + TABLENAME + " where id='IP' ORDER BY date DESC LIMIT 10;";
        ResultSet rs = query(sqlStr);
        try {
            while(rs.next()){
                ipList.add(rs.getString("value"));
            }
        } catch (Exception exp) {
            view.showError(Util.getStackTrace(exp));
        }
        return ipList;
    }




}
