package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbSimpleAuthService implements AuthService {
    private static Connection connection;
    private static Statement stmt;
    private static PreparedStatement psInsert;
    private static PreparedStatement regInsert;

    private class UserData {
        String login;
        String password;
        String nickname;

        public UserData(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }
    }

    private List<DbSimpleAuthService.UserData> users;


    public DbSimpleAuthService() {
//        users = new ArrayList<>();
//        try {
//            fillTable();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//        for (int i = 1; i <= 10; i++) {
//            users.add(new DbSimpleAuthService.UserData("l" + i, "p" + i, "nick" + i));
//        }
//
//
    }

    public static void fillTable() throws SQLException {
        connection.setAutoCommit(false);
        for (int i = 1; i <= 10; i++) {
            psInsert.setString(1, "l" + i + " ");
            psInsert.setString(2, "p" + i + " ");
            psInsert.setString(3, "nick" + i + " ");
            regInsert.executeUpdate();
            connection.setAutoCommit(true);
        }
    }



    public static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:main.db");
        stmt = connection.createStatement();
    }

    public static void disconnect() {
        try {
            stmt.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void prepareAllStatement() throws SQLException {
        psInsert = connection.prepareStatement("INSERT INTO authorization (login,password,nick) VALUES (?,?,?);");
        regInsert = connection.prepareStatement("INSERT INTO authorization (login,password,nick) VALUES (?,?,?);");
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
            try {
                ResultSet rs = stmt.executeQuery("SELECT login,password FROM authorization");
            while (rs.next()) {
                if ((rs.getString("login")).equals(login) && (rs.getString("password")).equals(password)) {
                    return rs.getString("login");
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean registration(String login, String password, String nickname) {

        try {
            ResultSet rs = stmt.executeQuery("SELECT login,password,nick FROM authorization");
            while (rs.next()) {
                if ((rs.getString("login")).equals(login) && (rs.getString("nick")).equals(password)) {
                   return false;
                }
                break;
            }
            regInsert.setString(1  , login + " "  );
            regInsert.setString(2, password + " " );
            regInsert.setString(3, nickname + " ");
            regInsert.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return true;
    }

}
