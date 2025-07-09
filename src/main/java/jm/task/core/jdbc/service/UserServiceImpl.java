package jm.task.core.jdbc.service;

import jm.task.core.jdbc.dao.UserDao;
import jm.task.core.jdbc.dao.UserDaoJDBCImpl;
import jm.task.core.jdbc.model.User;

import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService {
    private static UserServiceImpl instance;
    private UserDao userDao;
    private LoggerService logger;

    public UserServiceImpl() throws SQLException {
        this.userDao = UserDaoJDBCImpl.getInstance();
        this.logger = LoggerService.getInstance();
    }

    public static synchronized UserServiceImpl getInstance() throws SQLException {
        if (instance == null) {
            instance = new UserServiceImpl();
        }
        return instance;
    }

    public void createUsersTable() {
        logger.debug("Создаем таблицу", "UserServiceImpl", "createUsersTable", null);

        userDao.createUsersTable();
    }

    public void dropUsersTable() {
        logger.debug("Удаляем таблицу", "UserServiceImpl", "dropUsersTable", null);
        userDao.dropUsersTable();
    }

    public void saveUser(String name, String lastName, byte age) {
        logger.debug("Сохраним пользователя:" + name + " " + lastName,
                "UserServiceImpl", "saveUser", null);
        userDao.saveUser(name, lastName, age);
    }

    public void removeUserById(long id) {
        logger.debug("Удалим чудика с ID:" + id,
                "UserServiceImpl", "removeUserById", id);
        userDao.removeUserById(id);
    }

    public List<User> getAllUsers() {
        logger.debug("Получим всех", "UserServiceImpl", "getAllUsers", null);
        List<User> users = userDao.getAllUsers();
        for (User user : users) {
            System.out.println(user);
        }
        return users;
    }

    public void cleanUsersTable() {
        logger.debug("Чистим таблицу", "UserServiceImpl", "cleanUsersTable", null);
        userDao.cleanUsersTable();
    }
}