package com.lijian;

import  java.sql.*;


public class  Main{
    final static String SQL_DRIVER =  "com.mysql.jdbc.Driver";
    final static String DB_URL = "jdbc:mysql://10.22.4.55:3306/test";

    static final String USER = "root";
    static final String PASS = "password";

    public static void main(String[] args) {
        Pool pool = new Pool(SQL_DRIVER, DB_URL, USER, PASS,10, 3, 3*60*60 );

        Connection  c = null;
        try {
            c = pool.getConnection();
            System.out.println(c);
            Statement st = c.createStatement();
            st.execute("update websites  set url = 'google' where id = 11");
            System.out.println("update ok");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                pool.PutConn(c);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
