package com.lijian;

import java.sql.*;
import java.time.*

public class DbCon{
    private Connection  con;
    private boolean isBusy;
    private LocalDateTime timeUsed;

    public DbCon(Connection con){
        this.con = con;
        this.isBusy = false;
        this.timeUsed = LocalDateTime.now();
    }

    public void setConn(Connection con){
        this.con = con;
    }
    public Connection getConn(){
        return this.con;
    }

    public void setBusy(boolean isBusy){
        this.isBusy = isBusy;
    }

    public boolean getBusy(){
        return this.isBusy;
    }

    public LocalDateTime getTimeUsed(){
        return this.timeUsed;
    }
    public void setTimeUsed(LocalDateTime t ){
        this.timeUsed = t ;
    }

    public void close() throws SQLException {
        this.con.close();
    }

}



