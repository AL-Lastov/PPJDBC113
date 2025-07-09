package jm.task.core.jdbc.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;


public class Util {
    public static final String DB_URL = "URL";
    public static final String DB_USER = "USER";
    public static final String DB_PASSWORD = "PASSWORD";

    private Util() {
        throw new AssertionError("Не делай этого!");
    }
    private static Properties properties = new Properties();
    public synchronized static String getProperty(String name) throws SQLException {
        if(properties.isEmpty()){
            try (InputStream input = Util.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (input == null) {
                    throw new RuntimeException("Файл db.properties не найден!");
                }
                properties.load(input);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return properties.getProperty(name);

    }




}
