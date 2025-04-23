package net.mamesosu.data;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
public class DataBase {

    String host;
    String database;
    String user;
    String password;

    public DataBase() {
        Dotenv dotenv = Dotenv.configure().load();
        this.host = dotenv.get("DB_HOST");
        this.database = dotenv.get("DB_DATABASE");
        this.user = dotenv.get("DB_USER");
        this.password = dotenv.get("DB_PASSWORD");
    }

    public Connection getConnection() throws SQLException{
            return DriverManager.getConnection(
                    "jdbc:mysql://" + host + "/" + database + "?useSSL=false",
                    user,
                    password
            );
    }
}
