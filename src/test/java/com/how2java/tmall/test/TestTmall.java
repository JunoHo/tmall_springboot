package com.how2java.tmall.test;

import org.apache.tomcat.jdbc.pool.interceptor.StatementCache;

import java.sql.*;

public class TestTmall {
    public static void main(String args[]){
        try {
            Class.forName("com.mysql.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/tmall_springboot?useUnicode=true&characterEncoding=UTF-8",
                "root",
                "admin");
             PreparedStatement ps=c.prepareStatement("insert into category values(null,?)");){
            for(int i=1;i<=9;i++) {
                ps.setString(1,"测试分类"+i);
                ps.execute();
            }
         } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
