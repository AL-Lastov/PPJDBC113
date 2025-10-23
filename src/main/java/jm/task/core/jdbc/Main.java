package jm.task.core.jdbc;


import jm.task.core.jdbc.service.UserService;
import jm.task.core.jdbc.service.UserServiceImpl;
import jm.task.core.jdbc.util.Util;
import java.sql.SQLException;



public class Main {
    public static void main(String[] args) {
        UserService userService = null;

        try {
            userService = new UserServiceImpl();
            userService.createUsersTable();

            userService.saveUser("SSS", "QQQ", (byte) 34);
            userService.saveUser("AAAA", "ZZZ", (byte) 56);
            userService.saveUser("EEEE", "RRR", (byte) 23);


            System.out.println("все");
            userService.getAllUsers();
            System.out.println("удаляем одного");
            userService.removeUserById(1);
            System.out.println("Все");
            userService.getAllUsers();


             userService.cleanUsersTable();


             userService.dropUsersTable();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (userService != null) {
                try {
                    Util.getInstance().closeConnection();
                } catch (SQLException e) {
                   e.printStackTrace();
                }
            }
        }
    }
}
