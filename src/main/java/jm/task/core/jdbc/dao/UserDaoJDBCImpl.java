package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {
    private final Util util;

    private static final String CREATE = "CREATE TABLE IF NOT EXISTS users (id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(25) not null, last_name VARCHAR(25) not null, age INT not null)";
    private static final String DROP = "DROP TABLE IF EXISTS users";
    private static final String SAVE = "INSERT INTO users (name, last_name, age) VALUES (?,?,?)";
    private static final String REMOVE_BY_ID = "DELETE FROM users WHERE id = ?";
    private static final String GET_ALL = "SELECT * FROM users";
    private static final String CLEAR = "TRUNCATE TABLE users";

    public UserDaoJDBCImpl() throws SQLException {
        this.util = Util.getInstance();
    }

    public void createUsersTable() {
        try (Connection conn = util.getConnection();
             Statement statement = conn.createStatement()) {

            statement.executeUpdate(CREATE);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dropUsersTable() {
        try (Connection connection = util.getConnection();
             Statement s = connection.createStatement()) {

            s.executeUpdate(DROP);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveUser(String name, String lastName, Byte age) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;

        try {
            connection = util.getConnection();
            connection.setAutoCommit(false);

            ps = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, lastName);
            ps.setInt(3, age);

            int count = ps.executeUpdate();
            if (count == 0) {
                throw new SQLException("Юзер не добавлен в базу!");
            }
            generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                long id = generatedKeys.getLong(1);
                connection.commit();
            } else {
                throw new SQLException("Юзер остался без Id");
            }
        } catch (SQLException e) {
            rollback(connection);
            e.printStackTrace();
        } finally {
            close(ps, generatedKeys);
            closeConnection(connection);
        }
    }

    public void removeUserById(long id) {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = util.getConnection();
            connection.setAutoCommit(false);

            ps = connection.prepareStatement(REMOVE_BY_ID);
            ps.setLong(1, id);
            int count = ps.executeUpdate();
            connection.commit();

        } catch (SQLException e) {
            rollback(connection);
            e.printStackTrace();
        } finally {
            close(ps, null);
            closeConnection(connection);
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = util.getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_ALL);
             ResultSet resultSet = ps.executeQuery()) {

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("last_name"),
                        resultSet.getByte("age")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void cleanUsersTable() {
        try (Connection connection = util.getConnection();
             Statement s = connection.createStatement()) {

            s.executeUpdate(CLEAR);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void rollback(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void close(Statement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

