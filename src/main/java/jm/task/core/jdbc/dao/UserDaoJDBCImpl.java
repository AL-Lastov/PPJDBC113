package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.service.LoggerService;
import jm.task.core.jdbc.util.Util;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {
    private static UserDaoJDBCImpl instance;
    private final Util util;
    private final LoggerService logger;


    private static final String CREATE = "CREATE TABLE IF NOT EXISTS users (id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(25) not null, last_name VARCHAR(25) not null, age INT not null)";
    private static final String DROP = "DROP TABLE IF EXISTS users";
    private static final String SAVE = "INSERT INTO users (name, last_name, age) VALUES (?,?,?)";
    private static final String REMOVE_BY_ID = "DELETE FROM users WHERE id = ?";
    private static final String GET_ALL = "SELECT * FROM users";
    private static final String CLEAR = "TRUNCATE TABLE users";

    private UserDaoJDBCImpl() throws SQLException {
        this.util = Util.getInstance();
        this.logger = LoggerService.getInstance();
    }

    public static synchronized UserDaoJDBCImpl getInstance() throws SQLException {
        if (instance == null) {
            instance = new UserDaoJDBCImpl();
        }
        return instance;
    }

    public void createUsersTable() {
        try (Connection conn = util.getConnection();
             Statement statement = conn.createStatement()) {
            statement.executeUpdate(CREATE);
        } catch (SQLException e) {
            logger.error("Табличка не создана, во как " + e.getMessage(),
                    "UserDaoJDBCImpl", "createUsersTable", null);
        }
    }

    public void dropUsersTable() {
        try (Connection connection = util.getConnection();
             Statement s = connection.createStatement()) {
            s.executeUpdate(DROP);
        } catch (SQLException e) {
            logger.error("Табличка не удалена, так то " + e.getMessage(),
                    "UserDaoJDBCImpl", "dropUsersTable", null);
        }
    }


    public void saveUser(String name, String lastName, Byte age) {
        try (Connection connection = util.getConnection();
             PreparedStatement ps = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, lastName);
            ps.setInt(3, age);

            int count = ps.executeUpdate();
            if (count == 0) {
                throw new SQLException("ААА! Пользователь не добавлен в базу!");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong(1);
                    logger.info("Юзер добавлен с Id:" + id,
                            "UserDaoJDBCImpl", "saveUser", id);
                } else {
                    throw new SQLException("Юзер осталлся без Id");
                }
            }
        } catch (SQLException e) {
            logger.error("Юзер не сохранен" + e.getMessage(),
                    "UserDaoJDBCImpl", "saveUser", null);
            ;
        }
    }

    public void removeUserById(long id) {
        try (Connection connection = util.getConnection();
             PreparedStatement ps = connection.prepareStatement(REMOVE_BY_ID)) {
            ps.setLong(1, id);
            int count = ps.executeUpdate();
            if (count > 0) {
                logger.info("Юзер удален с Id" + id,
                        "UserDaoJDBCImpl", "removeUserById", id);
            } else {
                logger.warn("Юзер с Id " + id + " не найден",
                        "UserDaoJDBCImpl", "removeUserById", id);
            }
        } catch (SQLException e) {
            logger.error("Ошибка удаления пользователя: " + e.getMessage(),
                    "UserDaoJDBCImpl", "removeUserById", id);
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try (Connection connection = util.getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_ALL);
             ResultSet resultSet = ps.executeQuery()) {
            while (resultSet.next()) {
//                User user = new User();
//                user.setId(resultSet.getLong("id"));
//                user.setName(resultSet.getString("name"));
//                user.setLastName(resultSet.getString("last_name"));
//                user.setAge(resultSet.getByte("age"));
//                users.add(user);
//                добавил конструктор с ID что бы код был читабельнее
                User user = new User(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("last_name"),
                        resultSet.getByte("age")
                );
                users.add(user);
            }
            logger.info("Получено " + users.size() + " юзеров",
                    "UserDaoJDBCImpl", "getAllUsers", null);
        } catch (SQLException e) {
            logger.error("Не получили юзера" + e.getMessage(),
                    "UserDaoJDBCImpl", "getAllUsers", null);
        }

        return users;
    }

    public void cleanUsersTable() {
        try (Connection connection = util.getConnection();
             Statement s = connection.createStatement()) {
            s.executeUpdate(CLEAR);
            logger.info("Таблица users очищена", "UserDaoJDBCImpl", "cleanUsersTable", null);
        } catch (SQLException e) {
            logger.error("Таблица не очищена" + e.getMessage(),
                    "UserDaoJDBCImpl", "cleanUsersTable", null);
        }
    }
}
