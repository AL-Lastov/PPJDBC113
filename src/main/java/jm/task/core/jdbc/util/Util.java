package jm.task.core.jdbc.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class Util {
    public static final String DB_URL = "URL";
    public static final String DB_USER = "USER";
    public static final String DB_PASSWORD = "PASSWORD";

    private static Util inst;
    private Connection conn;
    private static Properties properties = new Properties();

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver",e);
        }
    }

    private Util() {
        System.out.println("ky ky");
    }

    public static Util getInstance() {
        if (inst == null) {
            inst = new Util();
        }
        return inst;
    }

    public synchronized static String getProperty(String name) throws SQLException {
        if (properties.isEmpty()) {
            try (InputStream input = Util.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (input == null) {
                    throw new RuntimeException("Файл db.properties не найден!");
                }
                properties.load(input);
            } catch (IOException e) {
                throw new RuntimeException("Не грузится файл db.properrties", e);
            }
        }
        return properties.getProperty(name);
    }

    public Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(
                    getProperty(DB_URL),
                    getProperty(DB_USER),
                    getProperty(DB_PASSWORD));
        }
        return conn;
    }
    public void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}
