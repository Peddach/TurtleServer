package de.petropia.turtleServer.api.worlds;

import com.mysql.cj.jdbc.MysqlDataSource;
import de.petropia.turtleServer.server.TurtleServer;
import org.bukkit.World;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    private static final String HOSTNAME = TurtleServer.getInstance().getConfig().getString("WorldDatabase.Hostname");
    private static final int PORT = TurtleServer.getInstance().getConfig().getInt("WorldDatabase.Port");

    private static DataSource dataSource;

    /**
     * Connect to the database and test the connection
     */
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

    /**
     * Create all tables when necassary
     */
    private static void createTable(){
        try(Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(CREATE_TABLE)){
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Save a world to the database
     * @param id The lowercase world id. It has to be unique
     * @param data A zip file of the region directory as a byte array
     * @param environment The {@link org.bukkit.World.Environment} of the world for correct loading
     * @return A {@link WorldDatabaseResultRecord} as a representation of the database record or null when not found
     */
    public static CompletableFuture<Boolean> saveWorld(String id, byte[] data, World.Environment environment){
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(SAVE_WORLD)){
                statement.setString(1, id.toLowerCase());
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

    public static CompletableFuture<WorldDatabaseResultRecord> loadWorldFromDB(String worldID){
        String id = worldID.toLowerCase();
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(QUERY_WORLD); ResultSet resultSet = statement.executeQuery()){
                if(!resultSet.next()){
                    return null;
                }
                String loaded_id = resultSet.getString(1);
                byte[] loaded_data = resultSet.getBytes(2);
                World.Environment environment = World.Environment.valueOf(resultSet.getString(3));
                return new WorldDatabaseResultRecord(loaded_id, loaded_data, environment);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }
}
