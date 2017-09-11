package com.lijian;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Vector;

public class Pool {
    private String driver;  // 数据库驱动
    private String dbUrl;   // 数据库地址
    private String dbUname; // 数据库用户名
    private String dbPass;  // 数据库密码


    private int  capacity;  // 容量
    private int  increment; // 扩张量
    private long timeIdle ; // 有效时间( 单位s)

    private Vector<DbCon>  conPool ;

    public Pool(String driver, String dbUrl, String dbUname, String dbPass, int capacity, int increment, long timeIdle){
        this.driver = driver;
        this.dbUrl = dbUrl;
        this.dbUname = dbUname;
        this.dbPass = dbPass;

        this.capacity = capacity;
        this.increment = increment;
        this.timeIdle = timeIdle;

        try {
            this.createPool();
        } catch ( Exception e){
            e.printStackTrace();
        }
    }
    private void createPool() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (this.conPool != null) {
            return;
        }
        // 驱动器实例化
        Driver driver = (Driver)(Class.forName(this.driver).newInstance());
        DriverManager.registerDriver(driver);

        this.conPool = new Vector<>();
        this.createConns(this.increment);
    }
    private void createConns(int increSize) throws SQLException {
        if (this.conPool == null) {
            this.conPool = new Vector<>();
        }
        for (int i=1; i<= increSize; i++){
            if (this.capacity < this.conPool.size() + 1){
                return;
            }
            Connection con = DriverManager.getConnection(this.dbUrl, this.dbUname, this.dbPass);
            this.conPool.addElement(new DbCon(con));
        }
    }

    public synchronized Connection getConnection() throws SQLException {
        DbCon c = null;
        try{
            c = findFreeConn();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (c == null) {
            this.createConns(this.increment);
            c = findFreeConn();
        }

        return (c == null) ? c.getConn() : null;
    }

    private DbCon findFreeConn() throws SQLException {
        if (this.conPool == null) {
            return null;
        }
        for (int i = 0; i < this.conPool.size(); i++){
            DbCon con = this.conPool.get(i);
            if (con.getBusy()) {
                continue;
            }
            if ( (con.getTimeUsed().plusSeconds(this.timeIdle)).isAfter(LocalDateTime.now())) {
                con.close();
                con.setConn(null);

                Connection c = DriverManager.getConnection(this.dbUrl, this.dbUname, this.dbPass);
                con.setConn(c);
                con.setBusy(true);
                con.setTimeUsed(LocalDateTime.now());
                return con;
            }
            con.setBusy(true);
            return con;
        }
        return null;
    }

    public synchronized void PutConn(Connection con ) throws  SQLException{
        if ( this.conPool  == null) {
            if (con != null) con.close();
        }

        for (int i=0; i< this.conPool.size(); i++){
            DbCon  c  = this.conPool.get(i);
            if (c.getConn() == con) {
                c.setBusy(false);
                break;
            }
        }
    }

    public synchronized  void cleanConPool() throws SQLException {
        if ( this.conPool == null) {
            return;
        }

        for (int i=0; i< this.conPool.size(); i++){
            DbCon  c  = this.conPool.get(i);
            c.close();
            this.conPool.remove(i);
        }

        this.conPool = null;

    }
}
