package de.petropia.turtleServer.api.worlds;

import com.mysql.cj.jdbc.MysqlDataSource;
import de.petropia.turtleServer.server.TurtleServer;
import org.bukkit.World;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class WorldDatabase {

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Worlds (" +
            "id VARCHAR(50) PRIMARY KEY," +
            "data LONGBLOB NOT NULL," +
            "env VARCHAR(20) NOT NULL" +
            ")";
    private static final String QUERY_WORLD = "SELECT id, data, env FROM Worlds WHERE id = ?";
    private static final String SAVE_WORLD = "REPLACE INTO Worlds id, data, env VALUES (?, ?, ?, ?)";
    private static final String DELETE_WORLD = "DELETE FROM Worlds WHERE id = ?";

    private static final String USER = TurtleServer.getInstance().getConfig().getString("WorldDatabase.User");
    private static final String PASSWORD = TurtleServer.getInstance().getConfig().getString("WorldDatabase.Password");
    private static final String DATABASE = TurtleServer.getInstance().getConfig().getString("WorldDatabase.Database");
    private static final String HOSTNAME = TurtleServer.getInstance().getConfig().getString("WorldDatabase.HOSTNAME");
    private static final int PORT = TurtleServer.getInstance().getConfig().getInt("WorldDatabase.Port");

    private static DataSource dataSource;

    public static void connect(){
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setUser(USER);
        mysqlDataSource.setPassword(PASSWORD);
        mysqlDataSource.setServerName(HOSTNAME);
        mysqlDataSource.setDatabaseName(DATABASE);
        mysqlDataSource.setPort(PORT);
        try(Connection connection = mysqlDataSource.getConnection()) {
            if (connection.isValid(1000)) {
                TurtleServer.getInstance().getLogger().info("Sucessfully connected to Worlds Database");
            } else {
                TurtleServer.getInstance().getLogger().info("Connection to World Database Failed!!!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dataSource = mysqlDataSource;
        createTable();
    }

    private static void createTable(){
        try(Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(CREATE_TABLE)){
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static CompletableFuture<Boolean> saveWorld(String id, byte[] data, World.Environment environment){
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(SAVE_WORLD)){
                statement.setString(1, id);
                statement.setBytes(2, data);
                statement.setString(3, environment.name());
                statement.execute();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }
}
