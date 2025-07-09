package jm.task.core.jdbc;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.service.LoggerService;
import jm.task.core.jdbc.service.UserService;
import jm.task.core.jdbc.service.UserServiceImpl;

import java.sql.SQLException;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        try {
            LoggerService logger = LoggerService.getInstance();
            logger.setLogLevel(LoggerService.Level.DEBUG); // DEBUG, INFO, WARN, ERROR

            UserService userService = UserServiceImpl.getInstance();
            userService.createUsersTable();
            userService.saveUser("SSS", "QQQ", (byte) 34);
            userService.saveUser("AAAA", "ZZZ", (byte) 56);
            userService.saveUser("EEEE", "RRR", (byte) 23);


            System.out.println("Все пользователи:");
            userService.getAllUsers();
            System.out.println("удаляем первого:");
            userService.removeUserById(1);
            System.out.println("Все пользователи:");
            userService.getAllUsers();

//            // Очищаем таблицу
//            userService.cleanUsersTable();

        } catch (SQLException e) {
            System.err.println("Ошибка при работе с БД: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
