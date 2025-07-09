package jm.task.core.jdbc.service;

import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.time.LocalDateTime;

public class LoggerService {
    private static LoggerService inst;
    private Level level;
    private final Util util;

    public enum Level {
        ERROR, WARN, INFO, DEBUG, TRACE
    }
    private static final String INSERT_LOG = "INSERT INTO logs (level, message, class_name, method_name, user_id, timestamp) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String CREATE_LOGS_TABLE =
            "CREATE TABLE IF NOT EXISTS logs (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                    "level VARCHAR(10) NOT NULL, " +
                    "message TEXT NOT NULL, " +
                    "class_name VARCHAR(255) NOT NULL, " +
                    "method_name VARCHAR(255) NOT NULL, " +
                    "user_id BIGINT NULL, " +
                    "timestamp TIMESTAMP NOT NULL)";

    private LoggerService() throws SQLException {
        this.util = Util.getInstance();
        this.level = Level.INFO;
        createLogsTable(); // исправил название метода
    }

    public static synchronized LoggerService getInstance() throws SQLException {
        if (inst == null) {
            inst = new LoggerService();
        }
        return inst;
    }

    private void createLogsTable() {
        // Получаем соединение когда нужно
        try (Connection connection = util.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_LOGS_TABLE);
        } catch (SQLException e) {
            System.err.println("Ошибка создания таблицы логов: " + e.getMessage());
        }
    }

    // Установка уровня логирования
    public void setLogLevel(Level level) {
        this.level = level;
    }

    public Level getLogLevel() {
        return level;
    }

    // Проверка, нужно ли логировать на данном уровне
    private boolean shouldLog(Level level) {
        return level.ordinal() <= level.ordinal();
    }

    private void log(Level level, String message, String className, String methodName, Long userId) {
        if (!shouldLog(level)) {
            return;
        }

        try (Connection connection = util.getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_LOG))  {
            ps.setString(1, level.name());
            ps.setString(2, message);
            ps.setString(3, className);
            ps.setString(4, methodName);
            if (userId != null) {
                ps.setLong(5, userId);
            } else {
                ps.setNull(5, Types.BIGINT);
            }
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка записи лога: " + e.getMessage());
        }
    }

    // Методы для разных уровней логирования
    public void error(String message, String className, String methodName, Long userId) {
        log(Level.ERROR, message, className, methodName, userId);
        System.err.println("ERROR: " + message);
    }

    public void warn(String message, String className, String methodName, Long userId) {
        log(Level.WARN, message, className, methodName, userId);
        System.out.println("WARN: " + message);
    }

    public void info(String message, String className, String methodName, Long userId) {
        log(Level.INFO, message, className, methodName, userId);
        System.out.println("INFO: " + message);
    }

    public void debug(String message, String className, String methodName, Long userId) {
        log(Level.DEBUG, message, className, methodName, userId);
        System.out.println("DEBUG: " + message);
    }

    public void trace(String message, String className, String methodName, Long userId) {
        log(Level.TRACE, message, className, methodName, userId);
        System.out.println("TRACE: " + message);
    }

}
