package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static jm.task.core.jdbc.util.Util.*;

public class UserDaoJDBCImpl implements UserDao {
    private static Connection getConnection() throws SQLException {

        Connection connection = DriverManager.getConnection(
                Util.getProperty(Util.DB_URL),
                Util.getProperty(Util.DB_USER),
                Util.getProperty(Util.DB_PASSWORD));

        return connection;
    }

    private static final String CREATE =
            "CREATE TABLE IF NOT EXISTS users (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                    "name VARCHAR(25) not null ," +
                    "last_name VARCHAR(25) not null ," +
                    "age INT not null)";
    private static final String DROP = "DROP TABLE IF EXISTS users";
    private static final String SAVE = "INSERT INTO users (name, last_name, age) VALUES (?,?,?)";
    private static final String REMOVE_BY_ID = "DELETE FROM users WHERE id = ?";
    private static final String GET_ALL = "SELECT * FROM users";
    private static final String CLEAR = "TRUNCATE TABLE users";

    public UserDaoJDBCImpl() {
    }

    public void createUsersTable() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE);
        } catch (SQLException e) {
            System.err.println("Таблица не создана: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public void dropUsersTable() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(DROP);
        } catch (SQLException e) {
            System.err.println("Таблица  не  удалена: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }


    public void saveUser(String name, String lastName, Byte age) {
        String ifNullName = (name == null) ? "" : name;
        String ifNullLastName = (lastName == null) ? "" : lastName;
        Byte ifNullAge = (age == null) ? 0 : age;
        //Замена конструктора byte на Byte
        // byte не может работать с ==null



        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ifNullName);
            ps.setString(2, ifNullLastName);
            ps.setInt(3, ifNullAge);

            int insertControl = ps.executeUpdate();
            if (insertControl == 0) {
                throw new SQLException("Пользователь не добавлен в базу!");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong(1);
                    System.out.println("Пользователь добавлен с номером: " + id);
                } else {
                    throw new SQLException("Не получилось добавить ID пользователю");
                }
            }
        } catch (SQLException e) {
            System.err.println("Пользователь не    сохранен " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public void removeUserById(long id) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(REMOVE_BY_ID)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Пользователь c с номером" + id + "не удален: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
//                User user = new User();
//                user.setId(resultSet.getLong("id"));
//                user.setName(resultSet.getString("name"));
//                user.setLastName(resultSet.getString("last_name"));
//                user.setAge(resultSet.getByte("age"));
//                users.add(user);
//                добавил конструктор с ID что бы тут код был читабельнее
                User user = new User(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("last_name"),
                        resultSet.getByte("age")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Не удается вывести списк пользователей " + e.getMessage());
            e.printStackTrace(System.err);
        }

        return users;
    }

    public void cleanUsersTable() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CLEAR);
        } catch (SQLException e) {
            System.err.println("Таблица пользователь не очищена: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
