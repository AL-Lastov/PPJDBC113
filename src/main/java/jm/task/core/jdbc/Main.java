package jm.task.core.jdbc;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.service.UserService;
import jm.task.core.jdbc.service.UserServiceImpl;

import java.util.List;


public class Main {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        userService.createUsersTable();
        userService.saveUser("aaa", null, (byte) 12);
        userService.saveUser("rrr", "ggg", (byte) 13);
        userService.saveUser(null, "nnn", (byte) 14);

        List<User> users = userService.getAllUsers();
        for (User user : users) {
            System.out.println(user);

        }
        userService.cleanUsersTable();
        userService.dropUsersTable();

    }
}
