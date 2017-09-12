package com.lijian;

import  java.sql.*;


public class  Main {
    final static String SQL_DRIVER = "com.mysql.jdbc.Driver";
    final static String DB_URL = "jdbc:mysql://10.22.4.55:3306/test";

    static final String USER = "root";
    static final String PASS = "password";

    public static void main(String[] args) {
        Pool pool = new Pool(SQL_DRIVER, DB_URL, USER, PASS, 10, 3, 3 * 60 * 60);

        Thread t1 = new Thread(new Db_job(pool,"update websites  set url = 'google.com' where id = 11" ), "test1");
        Thread t2 = new Thread(new Db_job(pool,"update websites  set url = 'facebook.com' where id = 5"), "test2");

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 关闭连接池
        try {
            pool.cleanConPool();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static class Db_job implements Runnable {
        private Pool p;
        private String sql;
        public Db_job(Pool p, String sql){
            this.p = p;
            this.sql = sql;
        }
        public void run() {
            Connection c = null;
            Statement st = null;
            try {
                c = p.getConnection();
                System.out.println(c);
                st = c.createStatement();
                st.execute(sql);
                c.commit();
                System.out.println("update ok");
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    st.close();
                    p.PutConn(c);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
