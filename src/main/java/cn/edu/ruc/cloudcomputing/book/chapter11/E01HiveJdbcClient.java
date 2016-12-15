package cn.edu.ruc.cloudcomputing.book.chapter11;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class E01HiveJdbcClient {
    /**
     * @param args
     * @throws SQLException
     */
    public static void main(String[] args) throws SQLException {
        // 注册JDBC驱动
        try {
            Class.forName("org.apache.hadoop.hive.jdbc.HiveDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // 建立和Hive数据库的连接
        Connection con = DriverManager.getConnection("jdbc:hive://master:10000/default", "", "");

        // 连接创建之后，就可以执行正常的数据库语句操作了

        // statement用来执行SQL语句
        Statement stmt = con.createStatement();

        // 下面为Hive测试语句
        String tableName = "u1_data";
        stmt.executeQuery("drop table " + tableName);
        ResultSet res = stmt.executeQuery("create table " + tableName + " (userid int, " + "movieid int,"
                + "rating int," + "city string," + "viewTime string)" + "row format delimited "
                + "fields terminated by '\t' " + "stored as textfile"); // 创建表
        // show tables语句
        String sql = "show tables";
        System.out.println("Running: " + sql + ":");
        res = stmt.executeQuery(sql);
        if (res.next()) {
            System.out.println(res.getString(1));
        }
        // describe table语句
        sql = "describe " + tableName;
        System.out.println("Running: " + sql);
        res = stmt.executeQuery(sql);
        while (res.next()) {
            System.out.println(res.getString(1) + "\t" + res.getString(2));
        }

        // load data语句
        String filepath = "/home/hadoop/Downloads/u.data.new";
        sql = "load data local inpath '" + filepath + "' overwrite into table " + tableName;
        System.out.println("Running: " + sql);
        res = stmt.executeQuery(sql);

        // select query: 选取前5条记录
        sql = "select * from " + tableName + " limit 5";
        System.out.println("Running: " + sql);
        res = stmt.executeQuery(sql);
        while (res.next()) {
            System.out.println(String.valueOf(res.getString(3) + "\t" + res.getString(4)));
        }

        // regular hive query
        sql = "select count(*) from " + tableName;
        System.out.println("Running: " + sql);
        res = stmt.executeQuery(sql);
        while (res.next()) {
            System.out.println(res.getString(1));
        }
    }
}
